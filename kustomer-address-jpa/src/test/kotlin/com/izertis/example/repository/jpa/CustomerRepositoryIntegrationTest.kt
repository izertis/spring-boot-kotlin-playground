package com.izertis.example.repository.jpa

import com.izertis.example.domain.Address
import com.izertis.example.domain.Customer
import com.izertis.example.domain.PaymentMethod
import com.izertis.example.domain.PaymentMethodType
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CustomerRepositoryIntegrationTest : BaseRepositoryIntegrationTest() {

    @Autowired
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var customerRepository: CustomerRepository

    private val id = 1L

    @Test
    fun findAllTest() {
        val results = customerRepository.findAll()
        assertFalse(results.isEmpty())
    }

    @Test
    fun findByIdTest() {
        val customer = customerRepository.findById(id).orElseThrow()
        assertNotNull(customer.id)
        assertNotNull(customer.version)
        assertNotNull(customer.createdBy)
        assertNotNull(customer.createdDate)
    }

    @Test
    fun saveTest() {
        val customer = Customer().apply {
            name = "Jane Smith"
            email = "jane.smith@example.com"
            addresses = mutableListOf(Address().apply {
                street = "456 Elm St"
                city = "Othertown"
            })
        }

        // OneToMany paymentMethods owner: true
        val paymentMethod = PaymentMethod().apply {
            type = PaymentMethodType.VISA
            cardNumber = "6543210987654321"
        }
        customer.addPaymentMethods(paymentMethod)

        // Persist aggregate root
        val created = customerRepository.save(customer)

        // reloading to get relationships persisted by id
        entityManager.flush()
        entityManager.refresh(created)

        assertNotNull(created.id)
        assertNotNull(created.version)
        assertNotNull(created.createdBy)
        assertNotNull(created.createdDate)

        assertTrue(customer.paymentMethods?.all { it.id != null } == true)
    }

    @Test
    fun updateTest() {
        val customer = customerRepository.findById(id).orElseThrow()
        customer.name = "updated"
        customer.email = "updated@email.com"

        val updated = customerRepository.save(customer)
        assertEquals("updated", updated.name)
        assertEquals("updated@email.com", updated.email)
    }

    @Test
    fun deleteTest() {
        customerRepository.deleteById(id)
        val notFound = customerRepository.findById(id)
        assertFalse(notFound.isPresent)
    }
}
