package com.izertis.example.repository.jpa.inmemory

import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.Example
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.FluentQuery
import org.apache.commons.lang3.reflect.FieldUtils

import java.util.ArrayList
import java.util.HashMap
import java.util.Optional
import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.StreamSupport

import org.apache.commons.lang3.ObjectUtils.firstNonNull

open class InMemoryJpaRepository<T, ID> : JpaRepository<T, ID> {

    interface PrimaryKeyGenerator<ID> {
        fun next(): ID
    }

    private val entities: MutableMap<ID, T> = HashMap()

    // private val primaryKeyGenerator: PrimaryKeyGenerator<ID> = PrimaryKeyGenerator { UUID.randomUUID().toString() }
    private var nextId: Long = 0
    private val primaryKeyGenerator: PrimaryKeyGenerator<ID> = object : PrimaryKeyGenerator<ID> {
        override fun next(): ID = nextId++ as ID
    }

    fun clear() {
        nextId = 0
        entities.clear()
    }

    fun getEntities(): MutableMap<ID, T> {
        return entities
    }

    fun containsKey(key: ID): Boolean {
        return entities.containsKey(key)
    }

    fun containsEntity(entity: T): Boolean {
        return entities.containsValue(entity)
    }

    protected fun <F> readField(target: T, fieldName: String): F {
        try {
            @Suppress("UNCHECKED_CAST")
            return FieldUtils.readField(target, fieldName, true) as F
        } catch (e: IllegalAccessException) {
            throw IllegalArgumentException("Field not found or not accessible: $fieldName")
        }
    }

    protected fun writeField(target: T, fieldName: String, value: Any?) {
        try {
            FieldUtils.writeField(target, fieldName, value, true)
        } catch (e: IllegalAccessException) {
            throw IllegalArgumentException("Field not found or not accessible: $fieldName")
        }
    }

    protected fun <F> findByField(fieldName: String, value: F): MutableList<T> {
        return entities.values.stream().filter { entity -> isSameValue(value, readField(entity, fieldName)) }.toList()
    }

    protected fun <F> findByUniqueField(fieldName: String, value: F): T? {
        val foundEntities = findByField(fieldName, value)
        return when {
            foundEntities.isEmpty() -> null
            foundEntities.size == 1 -> foundEntities[0]
            else -> throw IllegalArgumentException("Field $fieldName is not unique, found ${foundEntities.size} entities: $foundEntities")
        }
    }

    protected fun contains(values: Iterable<*>, value: Any?): Boolean {
        return StreamSupport.stream(values.spliterator(), false).anyMatch { e -> isSameValue(e, value) }
    }

    protected fun isSameValue(o1: Any?, o2: Any?): Boolean {
        return if (o1 == null) {
            o2 == null
        } else {
            o1 == o2
        }
    }

    override fun getReferenceById(id: ID): T & Any {
        return findByUniqueField("id", id) ?: throw EmptyResultDataAccessException("Entity with id $id not found", 1)
    }

    override fun getOne(id: ID): T {
        return getReferenceById(id)
    }

    override fun getById(id: ID): T {
        return getReferenceById(id)
    }

    override fun <S : T> save(entity: S): S {
        if (entity == null) {
            throw IllegalArgumentException("entity must not be null")
        }
        val id = firstNonNull(readField<ID>(entity, "id"), primaryKeyGenerator.next())
        writeField(entity, "id", id)
        entities[id] = entity
        return entity
    }

    override fun <S : T> saveAll(entitiesToSave: Iterable<S>): MutableList<S> {
        if (entitiesToSave == null) {
            throw IllegalArgumentException("entitiesToSave must not be null")
        }
        return StreamSupport.stream(entitiesToSave.spliterator(), false).map { e -> save(e) }.collect(Collectors.toList())
    }

    override fun flush() {
        // No-op
    }

    override fun <S : T> saveAndFlush(entity: S): S {
        return save(entity)
    }

    override fun <S : T> saveAllAndFlush(entities: Iterable<S>): MutableList<S> {
        return saveAll(entities)
    }

    override fun findById(id: ID): Optional<T> {
        return Optional.ofNullable(findByUniqueField("id", id)) as Optional<T>
    }

    override fun existsById(id: ID): Boolean {
        return findById(id).isPresent
    }

    override fun findAll(): MutableList<T> {
        return ArrayList(entities.values)
    }

    override fun findAllById(ids: Iterable<ID>): MutableList<T> {
        return entities.values.stream().filter { e -> contains(ids, readField(e, "id")) }.toList()
    }

    override fun count(): Long {
        return entities.size.toLong()
    }

    override fun deleteById(id: ID) {
        if (id == null) {
            throw IllegalArgumentException("id must not be null")
        }
        val removedEntity = entities.remove(id)
        if (removedEntity == null) {
            throw EmptyResultDataAccessException("Entity with id $id not found", 1)
        }
    }

    override fun delete(entity: T) {
        if (entity == null) {
            throw IllegalArgumentException("entity must not be null")
        }
        entities.remove(readField(entity, "id"))
    }

    override fun deleteAllById(ids: Iterable<ID>) {
        if (ids == null) {
            throw IllegalArgumentException("ids must not be null")
        }
        ids.forEach { id -> entities.remove(id) }
    }

    override fun deleteAll(entitiesToDelete: Iterable<T>) {
        if (entitiesToDelete == null) {
            throw IllegalArgumentException("entitiesToDelete must not be null")
        }
        for (entity in entitiesToDelete) {
            delete(entity)
        }
    }

    override fun deleteAll() {
        entities.clear()
    }

    override fun deleteAllInBatch(entities: Iterable<T>) {
        deleteAll(entities)
    }

    override fun deleteAllByIdInBatch(ids: Iterable<ID>) {
        deleteAllById(ids)
    }

    override fun deleteAllInBatch() {
        deleteAll()
    }

    override fun findAll(sort: Sort): MutableList<T> {
        // Sorting not implemented
        return findAll()
    }

    override fun findAll(pageable: Pageable): Page<T> {
        val total = count().toInt()
        val offset = pageable.offset.toInt()
        val limit = pageable.pageSize
        return PageImpl(findAll().subList(offset, Math.min(offset + limit, total)), pageable, total.toLong())
    }

    override fun <S : T> findOne(example: Example<S>): Optional<S> {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun <S : T> findAll(example: Example<S>): MutableList<S> {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun <S : T> findAll(example: Example<S>, sort: Sort): MutableList<S> {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun <S : T> findAll(example: Example<S>, pageable: Pageable): Page<S> {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun <S : T> count(example: Example<S>): Long {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun <S : T> exists(example: Example<S>): Boolean {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun <S : T, R> findBy(example: Example<S>, queryFunction: Function<FluentQuery.FetchableFluentQuery<S>, R>): R {
        throw UnsupportedOperationException("Not yet implemented")
    }
}
