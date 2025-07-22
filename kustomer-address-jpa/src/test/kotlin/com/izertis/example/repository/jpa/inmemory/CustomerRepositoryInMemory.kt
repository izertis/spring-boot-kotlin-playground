package com.izertis.example.repository.jpa.inmemory

import com.izertis.example.domain.Customer
import com.izertis.example.domain.PaymentMethod
import com.izertis.example.repository.jpa.CustomerRepository
import org.apache.commons.lang3.ObjectUtils
import java.util.function.Consumer

class CustomerRepositoryInMemory : InMemoryJpaRepository<Customer, Long>(), CustomerRepository {

    override fun <S : Customer> save(entity: S): S {
        super.save(entity)
        entity.paymentMethods.forEach { paymentMethod ->
            paymentMethod.id = paymentMethod.id ?: entity.id
        }
        return entity
    }
}
