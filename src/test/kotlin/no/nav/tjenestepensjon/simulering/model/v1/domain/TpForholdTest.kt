package no.nav.tjenestepensjon.simulering.model.v1.domain

import org.junit.jupiter.api.Test
import java.time.LocalDate.now
import java.util.Collections.singletonList
import org.junit.jupiter.api.Assertions.assertEquals


internal class TpForholdTest {
    @Test
    fun `Maps tp ordning to tp forhold`() {
        val stillingsprosentListe = singletonList(Stillingsprosent(
                datoFom = now(),
                datoTom = now(),
                stillingsprosent = 0.0,
                aldersgrense = 100,
                faktiskHovedlonn = "test",
                stillingsuavhengigTilleggslonn = "test2"
        ))
        val tpOrdning = TPOrdning("tssId", "tpId")
        val tpForhold = TpForhold(tpOrdning, stillingsprosentListe)
        assertEquals(tpOrdning.tpId, tpForhold.tpnr)
        assertEquals(tpOrdning.tssId, tpForhold.tssEksternId)
        assertEquals(1, tpForhold.stillingsprosentListe.size)
        stillingsprosentListe.forEachIndexed{ index, stillingsprosent ->
                assertEquals(stillingsprosent, tpForhold.stillingsprosentListe[index])
        }
    }
}