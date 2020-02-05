package no.nav.tjenestepensjon.simulering.mapper

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.ObjectFactory
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.tjenestepensjon.simulering.mapper.AFPPrivatMapper.mapToSimulertAFPPrivat
import org.junit.jupiter.api.Test

class AFPPrivatMapperTests {
    @Test
    fun test() {
        val original: `var` = SimulerAfpPrivat()
        original.setAfpOpptjeningTotalbelop(200000)
        original.setKompensasjonstillegg(1000.0)
        val excpected: `var` = ObjectFactory().createSimulertAFPPrivat()
        excpected.setAfpOpptjeningTotalbelop(200000)
        excpected.setKompensasjonstillegg(1000.0)
        val mapped: `var` = mapToSimulertAFPPrivat(original)
        assertEquals(excpected.getAfpOpptjeningTotalbelop(), mapped.getAfpOpptjeningTotalbelop())
        assertEquals(excpected.getKompensasjonstillegg(), mapped.getKompensasjonstillegg())
    }
}