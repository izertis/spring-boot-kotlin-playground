package com.izertis.example.repository.jpa.inmemory

import com.izertis.example.domain.*
import com.izertis.example.repository.jpa.CustomerRepository

class CustomerRepositoryInMemory : InMemoryJpaRepository<Customer, Long>(), CustomerRepository {
}
