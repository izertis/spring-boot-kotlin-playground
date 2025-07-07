package com.izertis.example.config;

import com.izertis.example.repository.jpa.CustomerRepository;
import com.izertis.example.repository.jpa.inmemory.CustomerRepositoryInMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

// @Configuration
public class RepositoriesInMemoryConfig {

    protected final CustomerRepository customerRepository = new CustomerRepositoryInMemory();

    @Bean
    @Primary
    public <T extends CustomerRepository> T customerRepository() {
        return (T) customerRepository;
    }

}
