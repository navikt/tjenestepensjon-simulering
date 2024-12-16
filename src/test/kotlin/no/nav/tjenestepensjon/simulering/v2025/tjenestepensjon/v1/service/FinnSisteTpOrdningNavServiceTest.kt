package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service

import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.FinnSisteTpOrdningService.Companion.TP_ORDNING_UTEN_ALIAS
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FinnSisteTpOrdningNavServiceTest{

    @Autowired
    private lateinit var finnSisteTpOrdningNavService: FinnSisteTpOrdningService

    @Test
    fun `finn siste ordning for tp-ordning uten alias`(){

        val sisteOrdning = finnSisteTpOrdningNavService.finnSisteOrdning(listOf(
            TpOrdningDto("navn", "tpNr", "orgNr", emptyList())
        ))

        assertNotNull(sisteOrdning)
        assertEquals(TP_ORDNING_UTEN_ALIAS, sisteOrdning)
    }

    @Test
    fun `finn siste ordning i en liste uten SPK retunerer foerste i listen`(){

        val tpOrdning = TpOrdningDto("navn", "tpNr", "orgNr", listOf("navn"))
        val sisteOrdning = finnSisteTpOrdningNavService.finnSisteOrdning(listOf(
            tpOrdning,
            TpOrdningDto("navn2", "tpNr2", "orgNr2", listOf("navn2")),
            TpOrdningDto("navn3", "tpNr3", "orgNr3", listOf("navn3"))
        ))

        assertNotNull(sisteOrdning)
        assertEquals(tpOrdning.alias.first(), sisteOrdning)
    }

    @Test
    fun `finn siste ordning i en liste MED SPK retunerer SPK med lowercase`(){

        val tpOrdning = TpOrdningDto("navn", "tpNr", "orgNr", listOf("navn"))
        val spk = TpOrdningDto("Statens Pensjonskasse", "tpNr2", "orgNr2", listOf("SPK"))
        val sisteOrdning = finnSisteTpOrdningNavService.finnSisteOrdning(listOf(
            tpOrdning,
            spk,
            TpOrdningDto("navn3", "tpNr3", "orgNr3", listOf("navn3"))
        ))

        assertNotNull(sisteOrdning)
        assertEquals(spk.alias.first().lowercase(), sisteOrdning)
    }
}