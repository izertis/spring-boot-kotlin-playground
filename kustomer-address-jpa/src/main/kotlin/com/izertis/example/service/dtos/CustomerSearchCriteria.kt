package com.izertis.example.service.dtos

import java.io.Serializable

/**
 * CustomerSearchCriteria.
 */
data class CustomerSearchCriteria(


    val name: String?  = null,


    val email: String?  = null,


    val city: String?  = null,


    val state: String?  = null

)  : Serializable {






}
