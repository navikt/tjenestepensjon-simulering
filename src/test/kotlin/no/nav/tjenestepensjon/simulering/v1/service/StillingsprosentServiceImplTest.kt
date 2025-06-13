package no.nav.tjenestepensjon.simulering.v1.service

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_OPPTJENINGSPERIODE_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_OPPTJENINGSPERIODE_TIME
import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningFullDto
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.testHelper.safeEq
import no.nav.tjenestepensjon.simulering.v1.soap.SPKStillingsprosentSoapClient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class StillingsprosentServiceImplTest {
    @Mock
    lateinit var metrics: AppMetrics

    @Mock
    lateinit var spkStillingsprosentSoapClient: SPKStillingsprosentSoapClient

    @InjectMocks
    lateinit var spkStillingsprosentService: SPKStillingsprosentServiceImpl

    private val fnr = "01011234567"

    @Test
    fun `Handles metrics`() {
        `when`(spkStillingsprosentSoapClient.getStillingsprosenter(anyNonNull(), anyNonNull())).thenReturn(emptyList())
        spkStillingsprosentService.getStillingsprosentListe(fnr, TpOrdningFullDto(tssId = "1", tpNr =  "1", navn = "Test Ordning"))
        verify(metrics).incrementCounter(safeEq(APP_NAME), safeEq(APP_TOTAL_OPPTJENINGSPERIODE_CALLS))
        verify(metrics).incrementCounter(safeEq(APP_NAME), safeEq(APP_TOTAL_OPPTJENINGSPERIODE_TIME), anyNonNull())
    }

}
