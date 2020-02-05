package no.nav.tjenestepensjon.simulering.mapper

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.TpForhold
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.mapper.TpForholdMapper.mapToTpForhold
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class TpForholdMapperTest {
    @Test
    fun mapsTpOrdningToTpForhold() {
        val stillingsprosent = Stillingsprosent()
        stillingsprosent.setDatoFom(LocalDate.now())
        stillingsprosent.setDatoTom(LocalDate.now())
        val tpOrdning = TPOrdning("tssId", "tpId")
        val tpForhold: List<TpForhold> = mapToTpForhold(Map.of(tpOrdning, java.util.List.of(stillingsprosent)))
        val mapped: TpForhold = tpForhold[0]
        assertThat(mapped.getTpnr(), `is`(tpOrdning.getTpId()))
        assertThat(mapped.getTssEksternId(), `is`(tpOrdning.getTssId()))
        assertThat(mapped.getStillingsprosentListe().size(), Matchers.`is`(1))
    }
}