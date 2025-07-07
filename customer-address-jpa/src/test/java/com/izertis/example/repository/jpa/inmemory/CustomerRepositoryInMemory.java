package com.izertis.example.repository.jpa.inmemory;

import com.izertis.example.domain.Customer;
import com.izertis.example.repository.jpa.CustomerRepository;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class CustomerRepositoryInMemory extends InMemoryJpaRepository<Customer> implements CustomerRepository {

    private long nextId = 0;
    private final PrimaryKeyGenerator<Long> primaryKeyGenerator = () -> nextId++;

    public Customer save(Customer entity) {
        entity = super.save(entity);
        entity.getPaymentMethods().forEach(paymentMethod -> {
            paymentMethod.setId(firstNonNull(paymentMethod.getId(), primaryKeyGenerator.next()));
        });
        return entity;
    }
}
