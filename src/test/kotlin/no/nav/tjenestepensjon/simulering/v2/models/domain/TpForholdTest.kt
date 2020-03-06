package no.nav.tjenestepensjon.simulering.v2.models.domain

import no.nav.tjenestepensjon.simulering.v2.models.defaultStillingsprosentListe
import no.nav.tjenestepensjon.simulering.v2.models.defaultTPOrdning
import no.nav.tjenestepensjon.simulering.v2.models.request.TpForhold
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


internal class TpForholdTest {
    @Test
    fun `Maps tp ordning to tp forhold`() {
        val tpForhold = TpForhold(defaultTPOrdning, defaultStillingsprosentListe)
        assertEquals(defaultTPOrdning.tpId, tpForhold.tpnr)
        assertEquals(defaultTPOrdning.tssId, tpForhold.)
        assertEquals(1, tpForhold.stillingsprosentListe.size)
        defaultStillingsprosentListe.forEachIndexed{ index, stillingsprosent ->
                assertEquals(stillingsprosent, tpForhold.stillingsprosentListe[index])
        }
    }
}