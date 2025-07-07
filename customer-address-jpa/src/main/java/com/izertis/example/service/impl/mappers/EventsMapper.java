package com.izertis.example.service.impl.mappers;

import com.izertis.example.events.dtos.Address;
import com.izertis.example.events.dtos.CustomerDeletedEvent;
import com.izertis.example.events.dtos.CustomerEvent;
import com.izertis.example.events.dtos.PaymentMethod;
import com.izertis.example.domain.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = { BaseMapper.class })
public interface EventsMapper {

    EventsMapper INSTANCE = Mappers.getMapper(EventsMapper.class);

    Address asAddress(com.izertis.example.domain.Address address);

    PaymentMethod asPaymentMethod(
            com.izertis.example.domain.PaymentMethod paymentMethod);

    CustomerEvent asCustomerEvent(
            Customer customer);

    CustomerDeletedEvent asCustomerEvent(Long id);

}
