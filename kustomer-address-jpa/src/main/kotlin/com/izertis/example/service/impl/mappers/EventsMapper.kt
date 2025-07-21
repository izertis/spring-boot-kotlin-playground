package com.izertis.example.service.impl.mappers

import com.izertis.example.domain.Address
import com.izertis.example.domain.Customer
import com.izertis.example.domain.PaymentMethod
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper(uses = [BaseMapper::class])
interface EventsMapper {

  companion object {
    val INSTANCE: EventsMapper = Mappers.getMapper(EventsMapper::class.java)
  }

  fun asAddress(address: Address): com.izertis.example.events.dtos.Address

  fun asPaymentMethod(paymentMethod: PaymentMethod): com.izertis.example.events.dtos.PaymentMethod

  fun asCustomerEvent(customer: Customer): com.izertis.example.events.dtos.CustomerEvent

  fun asCustomerEvent(id: Long): com.izertis.example.events.dtos.CustomerEvent {
    return com.izertis.example.events.dtos.CustomerEvent().apply { this.id = id }
  }
}
