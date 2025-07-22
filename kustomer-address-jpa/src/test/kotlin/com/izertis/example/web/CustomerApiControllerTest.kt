package com.izertis.example.web

import com.izertis.example.config.ServicesInMemoryConfig
import com.izertis.example.web.dtos.AddressDTO
import com.izertis.example.web.dtos.CustomerDTO
import com.izertis.example.web.dtos.CustomerSearchCriteriaDTO
import com.izertis.example.web.dtos.PaymentMethodDTO
import com.izertis.example.web.dtos.PaymentMethodTypeDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/** Test controller for CustomerApiController. */
class CustomerApiControllerTest {

  private val log: Logger = LoggerFactory.getLogger(javaClass)

  private val context = ServicesInMemoryConfig()
  private val controller = CustomerApiController(context.customerService())

  @BeforeEach
  fun setUp() {
    context.reloadTestData()
  }

  @Test
  fun createCustomerTest() {
    val reqBody = CustomerDTO(
      name = "John Doe",
      email = "john.doe@example.com",
      addresses = listOf(AddressDTO(street = "123 Main St", city = "Anytown")),
      paymentMethods = listOf(
        PaymentMethodDTO(
            id = null,
            version = null,
          type = PaymentMethodTypeDTO.VISA,
          cardNumber = "1234567890123456"
        )
      )
    )
    val response = controller.createCustomer(reqBody)
    Assertions.assertEquals(201, response.statusCode.value())
  }

  @Test
  fun getCustomerTest() {
    val customerId = 1L
    val response = controller.getCustomer(customerId)
    Assertions.assertEquals(200, response.statusCode.value())
  }

  @Test
  fun updateCustomerTest() {
    val customerId = 1L
    val reqBody = CustomerDTO(
      name = "Updated John Doe",
      email = "updated.john.doe@example.com",
      addresses = listOf(AddressDTO(street = "456 Updated St", city = "Updated City")),
      paymentMethods = listOf(
        PaymentMethodDTO(
          type = PaymentMethodTypeDTO.MASTERCARD,
          cardNumber = "6543210987654321"
        )
      )
    )
    val response = controller.updateCustomer(customerId, reqBody)
    Assertions.assertEquals(200, response.statusCode.value())
  }

  @Test
  fun deleteCustomerTest() {
    val customerId = 1L
    val response = controller.deleteCustomer(customerId)
    Assertions.assertEquals(204, response.statusCode.value())
  }

  @Test
  fun searchCustomersTest() {
    val page = 0
    val limit = 10
    val sort = listOf("name", "email")
    val reqBody = CustomerSearchCriteriaDTO(
      name = "John",
      email = "john@example.com"
    )
    val response = controller.searchCustomers(page, limit, sort, reqBody)
    Assertions.assertEquals(200, response.statusCode.value())
  }
}
