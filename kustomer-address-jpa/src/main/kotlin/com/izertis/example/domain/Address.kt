package com.izertis.example.domain

import java.io.Serializable
import jakarta.persistence.*
import jakarta.validation.constraints.*

/**
*
*/
// @Embeddable // json embedded
data class Address(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)

    var id: Long? = null,

    @Version
    var version: Int? = null,

    @NotNull @Size(max = 254)@Column(name = "street", nullable = false, length = 254)
    var street: String?  = null,

    @NotNull @Size(max = 254)@Column(name = "city", nullable = false, length = 254)
    var city: String?  = null



)  : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }

override fun toString(): String {
        return this::class.java.name + "#" + id
    }

    /* https://vladmihalcea.com/the-best-way-to-implement-equals-hashcode-and-tostring-with-jpa-and-hibernate/ */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Address) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

}
