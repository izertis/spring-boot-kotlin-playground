package com.izertis.example.web

import com.izertis.example.service.CustomerService
import com.izertis.example.web.mappers.CustomerDTOsMapper
import com.izertis.example.domain.*
import com.izertis.example.service.*
import com.izertis.example.service.dtos.*
import com.izertis.example.web.*
import com.izertis.example.web.dtos.*
import com.izertis.example.web.mappers.*

import java.math.*
import java.time.*
import java.util.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.web.context.request.NativeWebRequest


/**
 * REST controller for CustomerApi.
 */
@RestController
@RequestMapping("/api")
open class CustomerApiController(
    private val customerService: CustomerService
) : CustomerApi {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var request: NativeWebRequest

    private val mapper = CustomerDTOsMapper.INSTANCE

    fun getRequest(): Optional<NativeWebRequest> {
        return Optional.ofNullable(request)
    }



    override fun createCustomer(reqBody: CustomerDTO): ResponseEntity<CustomerDTO> {
        log.debug("REST request to createCustomer: {}", reqBody)
        val input = mapper.asCustomer(reqBody)
        val customer =  customerService.createCustomer(input)
        val responseDTO = mapper.asCustomerDTO(customer)
        return ResponseEntity.status(201).body(responseDTO)
    }

    override fun getCustomer(customerId: Long): ResponseEntity<CustomerDTO> {
        log.debug("REST request to getCustomer: {}", customerId)
        val customer =  customerService.getCustomer(customerId)
        return if (customer.isPresent) {
            val responseDTO = mapper.asCustomerDTO(customer.get())
            ResponseEntity.status(200).body(responseDTO)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    override fun updateCustomer(customerId: Long, reqBody: CustomerDTO): ResponseEntity<CustomerDTO> {
        log.debug("REST request to updateCustomer: {}, {}", customerId, reqBody)
        val input = mapper.asCustomer(reqBody)
        val customer =  customerService.updateCustomer(customerId, input)
        return if (customer.isPresent) {
            val responseDTO = mapper.asCustomerDTO(customer.get())
            ResponseEntity.status(200).body(responseDTO)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    override fun deleteCustomer(customerId: Long): ResponseEntity<Unit> {
        log.debug("REST request to deleteCustomer: {}", customerId)
        customerService.deleteCustomer(customerId)
        return ResponseEntity.status(204).build()
    }

    override fun searchCustomers(page: Int, limit: Int, sort: List<String>?, reqBody: CustomerSearchCriteriaDTO): ResponseEntity<CustomerPaginatedDTO> {
        log.debug("REST request to searchCustomers: {}, {}, {}, {}", page, limit, sort, reqBody)
        val input = mapper.asCustomerSearchCriteria(reqBody)
        val customerPage =  customerService.searchCustomers(input, pageOf(page, limit, sort))
        val responseDTO = mapper.asCustomerPaginatedDTO(customerPage)
        return ResponseEntity.status(200).body(responseDTO)
    }


    protected fun pageOf(page: Int?, limit: Int?, sort: List<String>?): Pageable {
        val sortOrder = sort?.let {
            Sort.by(it.map { sortParam ->
                val parts = sortParam.split(":")
                val property = parts[0]
                val direction = if (parts.size > 1) Sort.Direction.fromString(parts[1]) else Sort.Direction.ASC
                Sort.Order(direction, property)
            })
        } ?: Sort.unsorted()
        return PageRequest.of(page ?: 0, limit ?: 10, sortOrder)
    }
}
