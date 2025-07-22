package com.izertis.example.web.mappers

import com.izertis.example.domain.Customer
import com.izertis.example.service.dtos.CustomerSearchCriteria
import com.izertis.example.web.dtos.CustomerDTO
import com.izertis.example.web.dtos.CustomerPaginatedDTO
import com.izertis.example.web.dtos.CustomerSearchCriteriaDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page

@Mapper(uses = [BaseMapper::class], unmappedTargetPolicy = ReportingPolicy.IGNORE)
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
