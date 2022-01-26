package no.nav.tjenestepensjon.simulering.v1.models.domain

import no.nav.tjenestepensjon.simulering.defaultTPOrdning
import no.nav.tjenestepensjon.simulering.v1.models.defaultStillingsprosentListe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


internal class TpForholdTest {
    @Test
    fun `Maps tp ordning to tp forhold`() {
        TpForhold(defaultTPOrdning, defaultStillingsprosentListe).apply {
            assertEquals(defaultTPOrdning.tpId, tpnr)
            assertEquals(defaultTPOrdning.tssId, tssEksternId)
            assertEquals(1, stillingsprosentListe.size)
            defaultStillingsprosentListe.forEachIndexed { index, stillingsprosent ->
                assertEquals(stillingsprosent, stillingsprosentListe[index])
            }
        }
    }
}
