package com.izertis.example.web.mappers

import com.izertis.example.domain.Customer
import com.izertis.example.service.dtos.CustomerSearchCriteria
import com.izertis.example.web.mappers.*
import com.izertis.example.domain.*
import com.izertis.example.service.dtos.*
import com.izertis.example.web.dtos.*

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers
import java.math.*
import java.time.*
import java.util.*
import org.springframework.data.domain.Page

@Mapper(uses = [BaseMapper::class])
interface CustomerDTOsMapper {

    companion object {
        val INSTANCE: CustomerDTOsMapper = Mappers.getMapper(CustomerDTOsMapper::class.java)
    }

    // request mappings
    fun asCustomerSearchCriteria(dto: CustomerSearchCriteriaDTO): CustomerSearchCriteria
    fun asCustomer(dto: CustomerDTO): Customer

    // response mappings

    fun asCustomerDTOList(entityList: List<Customer>): List<CustomerDTO>
    @Mapping(target = "content", source = "content", conditionExpression = "java(page.getContent() != null)")
    fun asCustomerPaginatedDTO(page: Page<Customer>): CustomerPaginatedDTO
    fun asCustomerDTOPage(page: Page<Customer>): Page<CustomerDTO> {
        return page.map { this.asCustomerDTO(it) }
    }

    fun asCustomerDTO(entity: Customer): CustomerDTO


}
