package no.nav.tjenestepensjon.simulering.v3.afp

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class OffentligAFPYtelseBeregnerTest {

    @Test
    fun beregnAfpOffentligYtelse() {
        assertEquals(48_787.97, OffentligAFPYtelseBeregner.beregn(4_000_000, 19.07), 0.1)
        assertEquals(49785.24, OffentligAFPYtelseBeregner.beregn(2_500_000, 11.68), 0.1)
        assertEquals(17677.019, OffentligAFPYtelseBeregner.beregn(2_150_000, 28.29), 0.1)
        assertEquals(26190.84, OffentligAFPYtelseBeregner.beregn(1_753_213, 15.57), 0.1)
    }
}