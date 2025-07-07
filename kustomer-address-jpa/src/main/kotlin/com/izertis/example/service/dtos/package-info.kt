/** Inbound DTOs. */
@file:JvmName("PackageInfo")

package com.izertis.example.service.dtos

@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
annotation class Keep {
    // keeps this package even when it's empty,
    // allowing wildcard import "com.izertis.examples.core.inbound.dtos.*;"
}
