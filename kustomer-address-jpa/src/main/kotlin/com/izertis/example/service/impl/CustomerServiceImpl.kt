package com.izertis.example.service.impl

// import com.izertis.examples.events.dtos.*

import com.izertis.example.domain.Customer
import com.izertis.example.events.CustomerEventsProducer
import com.izertis.example.repository.jpa.CustomerRepository
import com.izertis.example.service.CustomerService
import com.izertis.example.service.dtos.CustomerSearchCriteria
import com.izertis.example.service.impl.mappers.CustomerServiceMapper
import com.izertis.example.service.impl.mappers.EventsMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/** Service Implementation for managing [com.izertis.example.domain.Customer]. */
@Service
@Transactional(readOnly = true)
open class CustomerServiceImpl(
    private val customerRepository: CustomerRepository,
    private val eventsProducer: CustomerEventsProducer
) : CustomerService {

  private val log: Logger = LoggerFactory.getLogger(javaClass)

  private val customerServiceMapper: CustomerServiceMapper = CustomerServiceMapper.INSTANCE

  private val eventsMapper: EventsMapper = EventsMapper.INSTANCE

  @Transactional
  override fun createCustomer(input: Customer): Customer {
    log.debug("[CRUD] Request to save Customer: {}", input)
    return customerServiceMapper.update(Customer(), input).let(customerRepository::save).also {
      eventsProducer.onCustomerEvent(eventsMapper.asCustomerEvent(it))
    }
  }

  override fun getCustomer(id: Long): Customer? {
    log.debug("[CRUD] Request to get Customer : {}", id)
    val customer = customerRepository.findByIdOrNull(id)
    return customer
  }

  @Transactional
  override fun updateCustomer(id: Long, input: Customer): Customer? {
    log.debug("Request updateCustomer: {} {}", id, input)

    return customerRepository
        .findByIdOrNull(id)
        ?.let { customerServiceMapper.update(it, input) }
        ?.let { customerRepository.save(it) }
        ?.also { eventsProducer.onCustomerEvent(eventsMapper.asCustomerEvent(it)) }
  }

  @Transactional
  override fun deleteCustomer(id: Long): Unit {
    log.debug("[CRUD] Request to delete Customer : {}", id)
    customerRepository.deleteById(id)
    eventsProducer.onCustomerEvent(eventsMapper.asCustomerEvent(id))
  }

  override fun searchCustomers(input: CustomerSearchCriteria, pageable: Pageable): Page<Customer> {
    log.debug("Request searchCustomers: {} {}", input, pageable)
    val customers = customerRepository.findAll(pageable)
    return customers
  }
}
