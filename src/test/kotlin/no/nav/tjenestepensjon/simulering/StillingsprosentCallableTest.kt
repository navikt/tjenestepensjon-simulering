package no.nav.tjenestepensjon.simulering

import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.REST
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP
import no.nav.tjenestepensjon.simulering.exceptions.StillingsprosentCallableException
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
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
internal class StillingsprosentCallableTest {
    @Mock
    private lateinit var simuleringEndPointRouter: TjenestepensjonsimuleringEndpointRouter


    @Test
    @Throws(Exception::class)
    fun `Call shall return stillingsprosenter with soap`() {
        val stillingsprosenter: List<Stillingsprosent> = prepareStillingsprosenter()
        Mockito.`when`(simuleringEndPointRouter.getStillingsprosenter(anyNonNull(), anyNonNull(), anyNonNull())).thenReturn(stillingsprosenter)
        val result: List<Stillingsprosent> = StillingsprosentCallable(fnr, tpOrdning, soapTpLeverandor, simuleringEndPointRouter)()
        assertStillingsprosenter(stillingsprosenter, result)
    }

    @Test
    @Throws(Exception::class)
    fun `Call shall return stillingsprosenter with rest`() {
        val stillingsprosenter: List<Stillingsprosent> = prepareStillingsprosenter()
        Mockito.`when`(simuleringEndPointRouter.getStillingsprosenter(anyNonNull(), anyNonNull(), anyNonNull())).thenReturn(stillingsprosenter)
        val result: List<Stillingsprosent> = StillingsprosentCallable(fnr, tpOrdning, restTpLeverandor, simuleringEndPointRouter)()
        assertStillingsprosenter(stillingsprosenter, result)
    }

    @Test
    @Throws(Exception::class)
    fun `Exception shall be rethrown as StillingsprosentCallableException with soap`() {
        Mockito.`when`(simuleringEndPointRouter.getStillingsprosenter(anyNonNull(), anyNonNull(), anyNonNull())).thenThrow(WebServiceIOException("msg from cause"))
        val exception = assertThrows<StillingsprosentCallableException> { StillingsprosentCallable(fnr, tpOrdning,  soapTpLeverandor, simuleringEndPointRouter)() }
        assertEquals("Call to getStillingsprosenter failed with exception: org.springframework.ws.client.WebServiceIOException: msg from cause", exception.message)
        assertEquals(tpOrdning, exception.tpOrdning)
    }

    @Test
    @Throws(Exception::class)
    fun `Exception shall be rethrown as StillingsprosentCallableException with rest`() {
        Mockito.`when`(simuleringEndPointRouter.getStillingsprosenter(anyNonNull(), anyNonNull(), anyNonNull())).thenThrow(WebServiceIOException("msg from cause"))
        val exception = assertThrows<StillingsprosentCallableException> { StillingsprosentCallable(fnr, tpOrdning, restTpLeverandor, simuleringEndPointRouter)() }
        assertEquals("Call to getStillingsprosenter failed with exception: org.springframework.ws.client.WebServiceIOException: msg from cause", exception.message)
        assertEquals(tpOrdning, exception.tpOrdning)
    }

    private companion object {
        val fnr = FNR("01011234567")
        val tpOrdning = TPOrdning("tss1", "tp1")
        val soapTpLeverandor = TpLeverandor("lev", "url1", SOAP)
        val restTpLeverandor = TpLeverandor("lev", "url1", REST)

        fun prepareStillingsprosenter() = listOf(
                Stillingsprosent(
                        stillingsprosent = 100.0,
                        aldersgrense = 70,
                        datoFom = LocalDate.of(2018, 1, 2),
                        datoTom = LocalDate.of(2029, 12, 31),
                        faktiskHovedlonn = "hovedlønn1",
                        stillingsuavhengigTilleggslonn = "tilleggslønn1"

                ),
                Stillingsprosent(
                        stillingsprosent = 12.5,
                        aldersgrense = 67,
                        datoFom = LocalDate.of(2019, 2, 3),
                        datoTom = LocalDate.of(2035, 11, 30),
                        faktiskHovedlonn = "hovedlønn2",
                        stillingsuavhengigTilleggslonn = "tilleggslønn2"
                )
        )

        fun assertStillingsprosenter(expected: List<Stillingsprosent>, actual: List<Stillingsprosent>) {
            assertEquals(expected.size, actual.size)
            for (index in expected.indices) {
                assertEquals(expected[index], actual[index])
            }
        }
    }
}