package com.izertis.example.repository.jpa.inmemory;

import com.izertis.example.domain.Customer;
import com.izertis.example.repository.jpa.CustomerRepository;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class CustomerRepositoryInMemory extends InMemoryJpaRepository<Customer> implements CustomerRepository {

    public Customer save(Customer entity) {
        Customer finalEntity = super.save(entity);
        finalEntity.getPaymentMethods().forEach(paymentMethod -> {
            paymentMethod.setId(firstNonNull(paymentMethod.getId(), finalEntity.getId()));
        });
        return finalEntity;
    }
}
