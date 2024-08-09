package no.nav.tjenestepensjon.simulering.v1.models.domain

import no.nav.tjenestepensjon.simulering.defaultTPOrdningIdDto
import no.nav.tjenestepensjon.simulering.v1.models.defaultStillingsprosentListe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


internal class TpForholdTest {
    @Test
    fun `Maps tp ordning to tp forhold`() {
        TpForhold(defaultTPOrdningIdDto, defaultStillingsprosentListe).apply {
            assertEquals(defaultTPOrdningIdDto.tpId, tpnr)
            assertEquals(defaultTPOrdningIdDto.tssId, tssEksternId)
            assertEquals(1, stillingsprosentListe.size)
            defaultStillingsprosentListe.forEachIndexed { index, stillingsprosent ->
                assertEquals(stillingsprosent, stillingsprosentListe[index])
            }
        }
    }
}
