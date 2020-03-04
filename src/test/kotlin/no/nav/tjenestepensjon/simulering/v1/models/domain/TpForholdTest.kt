package no.nav.tjenestepensjon.simulering.v1.models.domain

import no.nav.tjenestepensjon.simulering.v1.models.defaultStillingsprosentListe
import no.nav.tjenestepensjon.simulering.v1.models.defaultTPOrdning
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


internal class TpForholdTest {
    @Test
    fun `Maps tp ordning to tp forhold`() {
        val tpForhold = TpForhold(defaultTPOrdning, defaultStillingsprosentListe)
        assertEquals(defaultTPOrdning.tpId, tpForhold.tpnr)
        assertEquals(defaultTPOrdning.tssId, tpForhold.tssEksternId)
        assertEquals(1, tpForhold.stillingsprosentListe.size)
        defaultStillingsprosentListe.forEachIndexed{ index, stillingsprosent ->
                assertEquals(stillingsprosent, tpForhold.stillingsprosentListe[index])
        }
    }
}