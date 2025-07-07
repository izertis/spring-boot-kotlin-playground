package com.izertis.example.service.impl

import com.izertis.example.config.ServicesInMemoryConfig
import com.izertis.example.repository.jpa.inmemory.CustomerRepositoryInMemory
import com.izertis.example.domain.*
import com.izertis.example.service.*

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue

/**
 * Acceptance Test for CustomerService.
 */
class CustomerServiceTest {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    val context = ServicesInMemoryConfig()
    val customerService: CustomerService = context.customerService()

    val customerRepository: CustomerRepositoryInMemory = context.customerRepository()


    @BeforeEach
    fun setUp() {
        context.reloadTestData()
    }


    @Test
    fun createCustomerTest() {
        val input: Customer = Customer() // TODO
        // TODO fill input data
        // input.name = ""
        // input.email = ""
        // input.addresses = List.of(Address())
        val customer = customerService.createCustomer(input)
        assertNotNull(customer.id)
        assertTrue(customerRepository.containsEntity(customer))
}

    @Test
    fun getCustomerTest() {
        val id: Long = 1L
        val customer = customerService.getCustomer(id)
        assertTrue(customer.isPresent)
}

    @Test
    fun updateCustomerTest() {
        val id: Long = 1L
        val input: Customer = Customer() // TODO
        // TODO fill input data
        // input.name = ""
        // input.email = ""
        // input.addresses = List.of(Address())
        // assertTrue(customerRepository.containsKey(id))
        val customer = customerService.updateCustomer(id, input)
        assertTrue(customer.isPresent)
        assertTrue(customerRepository.containsEntity(customer.get()))
}

    @Test
    fun deleteCustomerTest() {
        val id: Long = 1L
        // assertTrue(customerRepository.containsKey(id))
        customerService.deleteCustomer(id)
        // assertFalse(customerRepository.containsKey(id))
}

    @Test
    fun searchCustomersTest() {// TODO: implement this test
}

}
