package com.izertis.example.repository.jpa

import com.izertis.example.domain.Address
import com.izertis.example.domain.Customer
import com.izertis.example.domain.PaymentMethod
import com.izertis.example.domain.PaymentMethodType
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.List

class CustomerRepositoryIntegrationTest : BaseRepositoryIntegrationTest() {

  @Autowired lateinit var entityManager: EntityManager

  @Autowired lateinit var customerRepository: CustomerRepository

  @Test
  fun findAllTest() {
    val results = customerRepository.findAll()
    Assertions.assertFalse(results.isEmpty())
  }

  @Test
  fun findByIdTest() {
    val id = 1L
    val customer = customerRepository.findById(id).orElseThrow()
    Assertions.assertNotNull(customer.id)
    Assertions.assertNotNull(customer.version)
    Assertions.assertNotNull(customer.createdBy)
    Assertions.assertNotNull(customer.createdDate)
  }

  @Test
  fun saveTest() {
      val customer = Customer(
          name = "Jane Smith",
          email = "jane.smith@example.com",
          addresses = mutableListOf(Address(street = "456 Elm St", city = "Othertown")),
      )

// OneToMany paymentMethods owner: true
      val paymentMethod = PaymentMethod(
          type = PaymentMethodType.VISA,
          cardNumber = "6543210987654321"
      )
      customer.addPaymentMethods(paymentMethod)

    // Persist aggregate root
    val created = customerRepository.save(customer)

    // reloading to get relationships persisted by id
    entityManager.flush()
    entityManager.refresh(created)
    Assertions.assertNotNull(created.id)
    Assertions.assertNotNull(created.version)
    Assertions.assertNotNull(created.createdBy)
    Assertions.assertNotNull(created.createdDate)

    Assertions.assertTrue(
        customer.paymentMethods?.stream()?.allMatch { item -> item.id != null } == true)
  }

  @Test
  fun updateTest() {
    val id = 1L
    val customer = customerRepository.findById(id).orElseThrow()
    customer.name = ""
    customer.email = ""
    customer.addresses = List.of(Address())

    val updated = customerRepository.save(customer)
    Assertions.assertEquals("", updated.name)
    Assertions.assertEquals("", updated.email)
    Assertions.assertEquals(List.of(Address()), updated.addresses)
  }

  @Test
  fun deleteTest() {
    val id = 1L
    customerRepository.deleteById(id)
    val notFound = customerRepository.findById(id)
    Assertions.assertFalse(notFound.isPresent)
  }
}
