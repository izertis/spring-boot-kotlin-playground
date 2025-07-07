package com.izertis.example.repository.jpa

import com.izertis.example.domain.Customer

import org.springframework.data.jpa.repository.*
import org.springframework.stereotype.Repository

/**
 * Spring Data JPA repository for the Customer entity.
 */
@Suppress("unused")
@Repository
interface CustomerRepository : JpaRepository<Customer, Long> {
}
