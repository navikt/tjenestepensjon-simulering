package no.nav.tjenestepensjon.simulering.v2025.afp.v1

import no.nav.tjenestepensjon.simulering.model.domain.pen.Alder
import no.nav.tjenestepensjon.simulering.model.domain.pen.Delingstall
import no.nav.tjenestepensjon.simulering.model.domain.pen.SimulerAFPOffentligLivsvarigRequest
import no.nav.tjenestepensjon.simulering.model.domain.popp.AFPGrunnlagBeholdningPeriode
import no.nav.tjenestepensjon.simulering.service.AFPBeholdningClient
import no.nav.tjenestepensjon.simulering.service.PenClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import java.time.LocalDate

class AFPOffentligLivsvarigSimuleringServiceTest {

    @Test
    fun `simuler beregning av AFP Offentlig med uttak ved 62 aar`() {
        val afpBeholdningGrunnlagResponse = listOf(
            AFPGrunnlagBeholdningPeriode(5441510, LocalDate.of(2026, 1, 1)),
            AFPGrunnlagBeholdningPeriode(5513910, LocalDate.of(2027, 1, 1)
        ))
        val fodselsdato = LocalDate.of(1964, 11, 7)
        val lavesteAlderVedUttak = Alder(62, 0)
        val alderVedAarsskifte = Alder(62, 1)

        val afpBeholdningClient = mock<AFPBeholdningClient>{ on { simulerAFPBeholdningGrunnlag(any()) }.thenReturn(afpBeholdningGrunnlagResponse) }
        val delingstallClient = mock<PenClient> { on { hentDelingstall(any(), any()) }.thenReturn(listOf(Delingstall(lavesteAlderVedUttak, 20.37), Delingstall(alderVedAarsskifte, 20.31))) }
        val service = AFPOffentligLivsvarigSimuleringService(afpBeholdningClient, delingstallClient)

        val resultat = service.simuler(SimulerAFPOffentligLivsvarigRequest("07516443469", fodselsdato, listOf(), LocalDate.of(2026, 12, 1)))
        assertEquals(2, resultat.size)
        assertEquals(62134.38, resultat[0].afpYtelsePerAar, 0.01)
        assertEquals(LocalDate.of(2026, 12, 1), resultat[0].gjelderFraOgMed)
        assertEquals(62963.53, resultat[1].afpYtelsePerAar, 0.01)
        assertEquals(LocalDate.of(2027, 1, 1), resultat[1].gjelderFraOgMed)
    }
}