package no.nav.tjenestepensjon.simulering.v1

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningFullDto
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v1.service.StillingsprosentService
import no.nav.tjenestepensjon.simulering.v1.service.SPKStillingsprosentServiceImpl
import no.nav.tjenestepensjon.simulering.v1.soap.SPKStillingsprosentSoapClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
internal class OpptjeningsperiodeCallableTest {

    @Mock
    private lateinit var SPKStillingsprosentSoapClient: SPKStillingsprosentSoapClient

    @Mock
    private lateinit var metrics: AppMetrics

    @Test
    fun `Call shall return stillingsprosenter with soap`() {
        val stillingsprosentService: StillingsprosentService = SPKStillingsprosentServiceImpl(SPKStillingsprosentSoapClient, metrics)
        val stillingsprosenter = prepareStillingsprosenter()
        `when`(
            SPKStillingsprosentSoapClient.getStillingsprosenter(anyNonNull(), anyNonNull())
        ).thenReturn(stillingsprosenter)
        val result = stillingsprosentService.getStillingsprosentListe(fnr, tpOrdning)
        assertStillingsprosenter(stillingsprosenter, result)
    }

    private companion object {
        val fnr = "01011234567"
        val tpOrdning = TpOrdningFullDto(tssId = "tss1", tpNr =  "tp1", navn = "Test Ordning")

        fun prepareStillingsprosenter() = listOf(
            Stillingsprosent(
                stillingsprosent = 100.0,
                aldersgrense = 70,
                datoFom = LocalDate.of(2018, 1, 2),
                datoTom = LocalDate.of(2029, 12, 31),
                faktiskHovedlonn = "hovedlønn1",
                stillingsuavhengigTilleggslonn = "tilleggslønn1",
                utvidelse = null
            ), Stillingsprosent(
                stillingsprosent = 12.5,
                aldersgrense = 67,
                datoFom = LocalDate.of(2019, 2, 3),
                datoTom = LocalDate.of(2035, 11, 30),
                faktiskHovedlonn = "hovedlønn2",
                stillingsuavhengigTilleggslonn = "tilleggslønn2",
                utvidelse = null
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
