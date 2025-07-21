package com.izertis.example.service

import com.izertis.example.domain.Customer
import com.izertis.example.service.dtos.CustomerSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

/** Inbound Service Port for managing [com.izertis.example.domain.Customer]. */
interface CustomerService {

  /** With Events: [CustomerEvent]. */
  fun createCustomer(input: Customer): Customer

  /**  */
  fun getCustomer(id: Long): Customer?

  /** With Events: [CustomerEvent]. */
  fun updateCustomer(id: Long, input: Customer): Customer?

  /** With Events: [CustomerEvent]. */
  fun deleteCustomer(id: Long): Unit

  /**  */
  fun searchCustomers(input: CustomerSearchCriteria, pageable: Pageable): Page<Customer>
}
