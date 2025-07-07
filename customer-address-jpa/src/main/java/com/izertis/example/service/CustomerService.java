package com.izertis.example.service;

import com.izertis.example.domain.Customer;
import com.izertis.example.service.dtos.CustomerSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/** Inbound Service Port for managing [Customer]. */
public interface CustomerService {

    /** With Events: [CustomerEvent]. */
    public Customer createCustomer(Customer input);

    /** */
    public Optional<Customer> getCustomer(Long id);

    /** With Events: [CustomerEvent]. */
    public Optional<Customer> updateCustomer(Long id, Customer input);

    /** With Events: [CustomerEvent]. */
    public void deleteCustomer(Long id);

    /** */
    public Page<Customer> searchCustomers(CustomerSearchCriteria input, Pageable pageable);

}
