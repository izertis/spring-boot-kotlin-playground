package com.izertis.example.config

import com.izertis.example.domain.Address
import com.izertis.example.domain.Customer
import com.izertis.example.domain.PaymentMethod
import com.izertis.example.events.EventsProducerInMemoryContext
import com.izertis.example.service.CustomerService
import com.izertis.example.service.impl.CustomerServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

/** Services InMemory Config. It can be used standalone or with @SpringBootTest. */
@Configuration
@Profile("in-memory")
open class ServicesInMemoryConfig : RepositoriesInMemoryConfig() {

  protected val eventsProducerInMemoryContext = EventsProducerInMemoryContext()

  protected val customerService =
      CustomerServiceImpl(
          customerRepository(), eventsProducerInMemoryContext.customerEventsProducer())

  @Bean
  open fun <T : CustomerService> customerService(): T {
    return customerService as T
  }

  companion object {
    var _customers: List<Customer>? = null
  }

  fun reloadTestData() {

    val testDataLoader =
        TestDataLoader(listOf(Customer::class.java, Address::class.java, PaymentMethod::class.java))
    val customers =
        _customers ?: testDataLoader.loadCollectionTestDataAsObjects(Customer::class.java)
    customerRepository().deleteAll()
    customerRepository().saveAll(customers)
  }
}
