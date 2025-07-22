package com.izertis.example.web

import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod.*
import org.springframework.http.MediaType

/** Integration tests for the [CustomerApi] REST controller. */
open class CustomerApiIntegrationTest : BaseWebTestClientTest() {

  /** Test: createCustomer for OK. */
  @Test
  fun testCreateCustomer_201() {
    val requestBody =
        """
        {
          "email": "jane.doe@example.com",
          "name": "Jane Doe",
          "addresses": [
            {
              "city": "Othertown",
              "street": "456 Elm St"
            }
          ],
          "paymentMethods": [
            {
              "type": "VISA",
              "cardNumber": "6543210987654321"
            }
          ]
        }
        """

    webTestClient
        .method(POST)
        .uri("/api/customers")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus()
        .isEqualTo(201)
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.id")
        .isNotEmpty()
        .jsonPath("$.version")
        .isNotEmpty()
        .jsonPath("$.name")
        .isNotEmpty()
        .jsonPath("$.email")
        .isNotEmpty()
        .jsonPath("$.addresses")
        .isNotEmpty()
        .jsonPath("$.addresses")
        .isArray()
        .jsonPath("$.addresses[0].street")
        .isNotEmpty()
        .jsonPath("$.addresses[0].city")
        .isNotEmpty()
        .jsonPath("$.paymentMethods")
        .isNotEmpty()
        .jsonPath("$.paymentMethods")
        .isArray()
        .jsonPath("$.paymentMethods[0].id")
        .isNotEmpty()
        .jsonPath("$.paymentMethods[0].version")
        .isNotEmpty()
        .jsonPath("$.paymentMethods[0].type")
        .isNotEmpty()
        .jsonPath("$.paymentMethods[0].cardNumber")
        .isNotEmpty()
  }

  /** Test: getCustomer for OK. */
  @Test
  fun testGetCustomer_200() {
    val customerId = "1"

    webTestClient
        .method(GET)
        .uri("/api/customers/{customerId}", customerId)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(200)
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.id")
        .isNotEmpty()
        .jsonPath("$.version")
        .isNotEmpty()
        .jsonPath("$.name")
        .isNotEmpty()
        .jsonPath("$.email")
        .isNotEmpty()
        .jsonPath("$.addresses")
        .isNotEmpty()
        .jsonPath("$.addresses")
        .isArray()
        .jsonPath("$.addresses[0].street")
        .isNotEmpty()
        .jsonPath("$.addresses[0].city")
        .isNotEmpty()
        .jsonPath("$.paymentMethods")
        .isNotEmpty()
        .jsonPath("$.paymentMethods")
        .isArray()
        .jsonPath("$.paymentMethods[0].id")
        .isNotEmpty()
        .jsonPath("$.paymentMethods[0].version")
        .isNotEmpty()
        .jsonPath("$.paymentMethods[0].type")
        .isNotEmpty()
        .jsonPath("$.paymentMethods[0].cardNumber")
        .isNotEmpty()
  }

  /** Test: updateCustomer for OK. */
  @Test
  fun testUpdateCustomer_200() {
    val requestBody =
        """
        {
          "id": 1,
          "version": 1,
          "email": "john.doe@example.com",
          "name": "John Doe",
          "addresses": [
            {
              "city": "Anytown",
              "street": "123 Main St"
            }
          ],
          "paymentMethods": [
            {
              "type": "VISA",
              "customerId": 1,
              "cardNumber": "1234567890123456"
            }
          ]
        }
        """
    val customerId = "1"

    webTestClient
        .method(PUT)
        .uri("/api/customers/{customerId}", customerId)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus()
        .isEqualTo(200)
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.id")
        .isNotEmpty()
        .jsonPath("$.version")
        .isNotEmpty()
        .jsonPath("$.name")
        .isNotEmpty()
        .jsonPath("$.email")
        .isNotEmpty()
        .jsonPath("$.addresses")
        .isNotEmpty()
        .jsonPath("$.addresses")
        .isArray()
        .jsonPath("$.addresses[0].street")
        .isNotEmpty()
        .jsonPath("$.addresses[0].city")
        .isNotEmpty()
        .jsonPath("$.paymentMethods")
        .isNotEmpty()
        .jsonPath("$.paymentMethods")
        .isArray()
        .jsonPath("$.paymentMethods[0].id")
        .isNotEmpty()
        .jsonPath("$.paymentMethods[0].version")
        .isNotEmpty()
        .jsonPath("$.paymentMethods[0].type")
        .isNotEmpty()
        .jsonPath("$.paymentMethods[0].cardNumber")
        .isNotEmpty()
  }

  /** Test: deleteCustomer for OK. */
  @Test
  fun testDeleteCustomer_204() {
    val customerId = "1"

    webTestClient
        .method(DELETE)
        .uri("/api/customers/{customerId}", customerId)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(204)
  }

  /** Test: searchCustomers for OK. */
  @Test
  fun testSearchCustomers_200() {
    val requestBody =
        """
            {
              "name" : "Jane Doe",
              "email" : "jane.doe@example.com",
              "city" : "Othertownt",
              "state" : "NY"
            }
        """
      val page = "0"
      val limit = "10"
      val sort = "name:asc"

    webTestClient
        .method(POST)
        .uri({
          it.path("/api/customers/search")
              .queryParam("page", page)
              .queryParam("limit", limit)
              .queryParam("sort", sort)
              .build(page, limit, sort)
        })
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus()
        .isEqualTo(200)
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.number")
        .isNotEmpty()
        .jsonPath("$.numberOfElements")
        .isNotEmpty()
        .jsonPath("$.size")
        .isNotEmpty()
        .jsonPath("$.totalElements")
        .isNotEmpty()
        .jsonPath("$.totalPages")
        .isNotEmpty()
        .jsonPath("$.content")
        .isNotEmpty()
        .jsonPath("$.content")
        .isArray()
        .jsonPath("$.content[0].id")
        .isNotEmpty()
        .jsonPath("$.content[0].version")
        .isNotEmpty()
        .jsonPath("$.content[0].name")
        .isNotEmpty()
        .jsonPath("$.content[0].email")
        .isNotEmpty()
        .jsonPath("$.content[0].addresses")
        .isNotEmpty()
        .jsonPath("$.content[0].paymentMethods")
        .isNotEmpty()
  }
}
