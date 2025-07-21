package com.izertis.example.domain

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

/** Enum for PaymentMethodType. */
enum class PaymentMethodType(val value: Int) {

  VISA(1),
  MASTERCARD(2);

  companion object {
    fun fromValue(value: Int): PaymentMethodType? {
      return entries.find { it.value == value }
    }
  }

  @Converter
  class PaymentMethodTypeConverter : AttributeConverter<PaymentMethodType, Int> {
    override fun convertToDatabaseColumn(attribute: PaymentMethodType?): Int? {
      return attribute?.value
    }

    override fun convertToEntityAttribute(dbData: Int?): PaymentMethodType? {
      return if (dbData == null) null else PaymentMethodType.fromValue(dbData)
    }
  }
}
