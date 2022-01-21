package no.nav.tjenestepensjon.simulering.testHelper

import org.mockito.Mockito

inline fun <reified T> anyNonNull(): T = Mockito.any(T::class.java)
fun <T : Any> safeEq(value: T): T = Mockito.eq(value) ?: value
