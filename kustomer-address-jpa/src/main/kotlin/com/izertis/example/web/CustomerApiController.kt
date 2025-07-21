package com.izertis.example.web

import com.izertis.example.service.CustomerService
import com.izertis.example.web.dtos.CustomerDTO
import com.izertis.example.web.dtos.CustomerPaginatedDTO
import com.izertis.example.web.dtos.CustomerSearchCriteriaDTO
import com.izertis.example.web.mappers.CustomerDTOsMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.NativeWebRequest
import java.util.*

/** REST controller for CustomerApi. */
@RestController
@RequestMapping("/api")
open class CustomerApiController(private val customerService: CustomerService) : CustomerApi {

  private val log: Logger = LoggerFactory.getLogger(javaClass)

  @Autowired private lateinit var request: NativeWebRequest

  private val mapper = CustomerDTOsMapper.INSTANCE

  fun getRequest(): Optional<NativeWebRequest> {
    return Optional.ofNullable(request)
  }

  override fun createCustomer(reqBody: CustomerDTO): ResponseEntity<CustomerDTO> {
    log.debug("REST request to createCustomer: {}", reqBody)
    return mapper
        .asCustomer(reqBody)
        .let(customerService::createCustomer)
        .let(mapper::asCustomerDTO)
        .let { ResponseEntity.status(201).body(it) } ?: ResponseEntity.notFound().build()
  }

  override fun getCustomer(customerId: Long): ResponseEntity<CustomerDTO> {
    log.debug("REST request to getCustomer: {}", customerId)
    return customerService
        .getCustomer(customerId)
        ?.let(mapper::asCustomerDTO) //
        ?.let { ResponseEntity.status(200).body(it) } ?: ResponseEntity.notFound().build()
  }

  override fun updateCustomer(customerId: Long, reqBody: CustomerDTO): ResponseEntity<CustomerDTO> {
    log.debug("REST request to updateCustomer: {}, {}", customerId, reqBody)
    return mapper
        .asCustomer(reqBody)
        .let { customerService.updateCustomer(customerId, it) }
        ?.let(mapper::asCustomerDTO)
        ?.let { ResponseEntity.status(200).body(it) } ?: ResponseEntity.notFound().build()
  }

  override fun deleteCustomer(customerId: Long): ResponseEntity<Unit> {
    log.debug("REST request to deleteCustomer: {}", customerId)
    customerService.deleteCustomer(customerId)
    return ResponseEntity.status(204).build()
  }

  override fun searchCustomers(
      page: Int,
      limit: Int,
      sort: List<String>?,
      reqBody: CustomerSearchCriteriaDTO
  ): ResponseEntity<CustomerPaginatedDTO> {
    log.debug("REST request to searchCustomers: {}, {}, {}, {}", page, limit, sort, reqBody)
    return mapper
        .asCustomerSearchCriteria(reqBody)
        .let { customerService.searchCustomers(it, pageOf(page, limit, sort)) }
        .let(mapper::asCustomerPaginatedDTO)
        .let { ResponseEntity.status(200).body(it) }
  }

  protected fun pageOf(page: Int?, limit: Int?, sort: List<String>?): Pageable {
    val sortOrder =
        sort?.let {
          Sort.by(
              it.map { sortParam ->
                val parts = sortParam.split(":")
                val property = parts[0]
                val direction =
                    if (parts.size > 1) Sort.Direction.fromString(parts[1]) else Sort.Direction.ASC
                Sort.Order(direction, property)
              })
        } ?: Sort.unsorted()
    return PageRequest.of(page ?: 0, limit ?: 10, sortOrder)
  }
}
