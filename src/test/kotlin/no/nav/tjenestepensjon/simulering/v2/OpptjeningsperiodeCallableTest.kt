package no.nav.tjenestepensjon.simulering.v2

import no.nav.tjenestepensjon.simulering.v2.models.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.exceptions.StillingsprosentCallableException
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.v2.models.domain.Opptjeningsperiode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.ws.client.WebServiceIOException
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
internal class OpptjeningsperiodeCallableTest {
    @Mock
    private lateinit var simuleringEndPointRouter: TjenestepensjonsimuleringEndpointRouter


    @Test
    @Throws(Exception::class)
    fun `Call shall return stillingsprosenter with rest`() {
        val stillingsprosenter: List<Opptjeningsperiode> = prepareStillingsprosenter()
        Mockito.`when`(simuleringEndPointRouter.getStillingsprosenter(anyNonNull(), anyNonNull(), anyNonNull()))
                .thenReturn(stillingsprosenter)

        val result: List<Opptjeningsperiode> = OpptjeningsperiodeCallable(
                fnr,
                tpOrdning,
                restTpLeverandor,
                simuleringEndPointRouter
        )()
        assertStillingsprosenter(stillingsprosenter, result)
    }


    @Test
    @Throws(Exception::class)
    fun `Exception shall be rethrown as StillingsprosentCallableException with rest`() {
        Mockito.`when`(simuleringEndPointRouter.getStillingsprosenter(anyNonNull(), anyNonNull(), anyNonNull())).thenThrow(WebServiceIOException("msg from cause"))
        val exception = assertThrows<StillingsprosentCallableException> { OpptjeningsperiodeCallable(fnr, tpOrdning, restTpLeverandor, simuleringEndPointRouter)() }
        assertEquals("Call to getStillingsprosenter failed with exception: org.springframework.ws.client.WebServiceIOException: msg from cause", exception.message)
        assertEquals(tpOrdning, exception.tpOrdning)
    }

    private companion object {
        val fnr = FNR("01011234567")
        val tpOrdning = TPOrdning("tss1", "tp1")
        val restTpLeverandor = TpLeverandor("lev", "url1")

        fun prepareStillingsprosenter() = listOf(
                Opptjeningsperiode(
                        stillingsprosent = 100.0,
                        aldersgrense = 70,
                        datoFom = LocalDate.of(2018, 1, 2),
                        datoTom = LocalDate.of(2029, 12, 31),
                        faktiskHovedlonn = "hovedlønn1",
                        stillingsuavhengigTilleggslonn = "tilleggslønn1"

                ),
                Opptjeningsperiode(
                        stillingsprosent = 12.5,
                        aldersgrense = 67,
                        datoFom = LocalDate.of(2019, 2, 3),
                        datoTom = LocalDate.of(2035, 11, 30),
                        faktiskHovedlonn = "hovedlønn2",
                        stillingsuavhengigTilleggslonn = "tilleggslønn2"
                )
        )

        fun assertStillingsprosenter(expected: List<Opptjeningsperiode>, actual: List<Opptjeningsperiode>) {
            assertEquals(expected.size, actual.size)
            for (index in expected.indices) {
                assertEquals(expected[index], actual[index])
            }
        }
    }
}