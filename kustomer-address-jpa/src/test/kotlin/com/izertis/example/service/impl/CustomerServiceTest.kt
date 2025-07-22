package com.izertis.example.service.impl

import com.izertis.example.config.ServicesInMemoryConfig
import com.izertis.example.domain.Address
import com.izertis.example.domain.Customer
import com.izertis.example.repository.jpa.inmemory.CustomerRepositoryInMemory
import com.izertis.example.service.CustomerService
import com.izertis.example.service.dtos.CustomerSearchCriteria
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest

/** Acceptance Test for CustomerService. */
class CustomerServiceTest {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    private val context = ServicesInMemoryConfig()
    private val customerService: CustomerService = context.customerService()
    private val customerRepository: CustomerRepositoryInMemory = context.customerRepository()

    @BeforeEach
    fun setUp() {
        context.reloadTestData()
    }

    @Test
    fun createCustomerTest() {
        val input = Customer().apply {
            name = "name"
            email = "me@email.com"
            addresses = mutableListOf(Address().apply {
                street = "street"
                city = "city"
            })
        }

        val customer = customerService.createCustomer(input)
        assertNotNull(customer.id)
        assertTrue(customerRepository.containsEntity(customer))
    }

    @Test
    fun getCustomerTest() {
        val id = 1L
        val customer = customerService.getCustomer(id)
        assertNotNull(customer)
    }

    @Test
    fun updateCustomerTest() {
        val id = 1L
        val input = Customer().apply {
            name = "name"
            email = "me@email.com"
            addresses = mutableListOf(Address().apply {
                street = "street"
                city = "city"
            })
        }

        assertTrue(customerRepository.containsKey(id))
        val customer = customerService.updateCustomer(id, input)
        assertNotNull(customer)
        assertTrue(customerRepository.containsEntity(customer!!))
    }

    @Test
    fun deleteCustomerTest() {
        val id = 1L
        assertTrue(customerRepository.containsKey(id))
        customerService.deleteCustomer(id)
        assertFalse(customerRepository.containsKey(id))
    }

    @Test
    fun searchCustomersTest() {
        val searchCriteria = CustomerSearchCriteria()
        val results = customerService.searchCustomers(searchCriteria, PageRequest.of(0, 10))
        assertNotNull(results)
        assertFalse(results.isEmpty)
    }
}
