package com.izertis.example.config

import com.izertis.example.repository.jpa.inmemory.CustomerRepositoryInMemory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

// @Configuration
open class RepositoriesInMemoryConfig {

  protected val customerRepository = CustomerRepositoryInMemory()

  @Bean
  @Primary
  fun customerRepository(): CustomerRepositoryInMemory {
    return customerRepository
  }
}
