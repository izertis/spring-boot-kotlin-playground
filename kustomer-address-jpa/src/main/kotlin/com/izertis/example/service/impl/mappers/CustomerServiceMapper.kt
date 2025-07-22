package com.izertis.example.service.impl.mappers

import com.izertis.example.domain.Customer
import com.izertis.example.service.dtos.CustomerSearchCriteria
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers

@Mapper(uses = [BaseMapper::class])
interface CustomerServiceMapper {

  companion object {
    val INSTANCE: CustomerServiceMapper = Mappers.getMapper(CustomerServiceMapper::class.java)
  }

  // input mappings
  // Customer-Customer updateCustomer
  fun asCustomer(input: Customer): Customer

  @Mapping(target = "id", ignore = true)
  fun update(@MappingTarget entity: Customer, input: Customer): Customer

  // output mappings
  @AfterMapping
  fun manageRelationships(@MappingTarget entity: Customer) {
    entity.paymentMethods.forEach { paymentMethods -> paymentMethods.customer = entity }
  }
}
