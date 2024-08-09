package no.nav.tjenestepensjon.simulering.v2.models.domain

import no.nav.tjenestepensjon.simulering.defaultTPOrdningIdDto
import no.nav.tjenestepensjon.simulering.defaultTpid
import no.nav.tjenestepensjon.simulering.v2.models.defaultOpptjeningsperiodeListe
import no.nav.tjenestepensjon.simulering.v2.models.request.TpForhold
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


internal class TpForholdTest {
    @Test
    fun `Maps tp ordning to tp forhold`() {
        val tpForhold = TpForhold(defaultTpid, defaultOpptjeningsperiodeListe)

        assertEquals(defaultTPOrdningIdDto.tpId, tpForhold.tpnr)
        //assertEquals(defaultTPOrdning.tssId, tpForhold.tssId) //todo what to do here
        assertEquals(1, tpForhold.opptjeningsperiodeListe.size)
        defaultOpptjeningsperiodeListe.forEachIndexed { index, opptjeningsperiode ->
            assertEquals(opptjeningsperiode, tpForhold.opptjeningsperiodeListe[index])
        }
    }
}
