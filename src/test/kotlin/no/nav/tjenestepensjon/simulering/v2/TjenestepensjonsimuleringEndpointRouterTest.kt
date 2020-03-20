package no.nav.tjenestepensjon.simulering.v2

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_SIMULERING_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_SIMULERING_TIME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_TIME
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.testHelper.safeEq
import no.nav.tjenestepensjon.simulering.v2.models.domain.Opptjeningsperiode
import no.nav.tjenestepensjon.simulering.v2.models.domain.SivilstandCodeEnum.REPA
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v2.rest.RestClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
internal class TjenestepensjonsimuleringEndpointRouterTest {

    private val fnr = FNR("01011234567")

    private val opptjeningsperiodeList = listOf(
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

    private val responseMockData = SimulerOffentligTjenestepensjonResponse(
            tpnr = "",
            navnOrdning = ""
    )

    private val simulerPensjonRequest = SimulerPensjonRequest(
            fnr = fnr,
            fodselsdato = "",
            sisteTpnr = "",
            inntektListe = emptyList(),
            sivilstandkode = REPA,
            simuleringsperiodeListe = emptyList(),
            simuleringsdataListe = emptyList(),
            tpForholdListe = emptyList()
    )

    @Mock
    private lateinit var restClient: RestClient
    @Mock
    private lateinit var metrics: AppMetrics
    @InjectMocks
    private lateinit var simuleringEndpointRouter: TjenestepensjonsimuleringEndpointRouter

    private val tpOrdning = TPOrdning("tss1", "tp1")
    private val tpRestLeverandor = TpLeverandor("lev", "url1", null)


    @Test
    fun `Call shall return opptjeningsperiodeList with rest`() {
        Mockito.`when`(restClient.getOpptjeningsperiode(anyNonNull(), anyNonNull(), anyNonNull())).thenReturn(opptjeningsperiodeList)
        val result: List<Opptjeningsperiode> = simuleringEndpointRouter.getOpptjeningsperiodeListe(
                fnr,
                tpOrdning,
                tpRestLeverandor
        )

        Mockito.verify<AppMetrics>(metrics).incrementCounter(safeEq(tpRestLeverandor.name), safeEq(TP_TOTAL_STILLINGSPROSENT_CALLS))
        Mockito.verify<AppMetrics>(metrics).incrementCounter(safeEq(tpRestLeverandor.name), safeEq(TP_TOTAL_STILLINGSPROSENT_TIME), anyNonNull())
        assertOpptjeningsperiodeList(result)
    }

//    @Test
//    fun `Call shall return SimulerOffentligTjenestepensjonResponse with rest`() {
//        Mockito.`when`(restClient.getResponse(anyNonNull(), anyNonNull(), anyNonNull(), anyNonNull())).thenReturn(responseMockData)
//        val result: SimulerOffentligTjenestepensjonResponse = simuleringEndpointRouter.simulerPensjon(
//                simulerPensjonRequest,
//                tpOrdning,
//                tpRestLeverandor,
//                emptyMap()
//        )
//        Mockito.verify<AppMetrics>(metrics).incrementCounter(safeEq(tpRestLeverandor.name), safeEq(TP_TOTAL_SIMULERING_CALLS))
//        Mockito.verify<AppMetrics>(metrics).incrementCounter(safeEq(tpRestLeverandor.name), safeEq(TP_TOTAL_SIMULERING_TIME), anyNonNull())
//        assertEquals(result, responseMockData)
//    }

    private fun assertOpptjeningsperiodeList(actual: List<Opptjeningsperiode>) {
        assertEquals(opptjeningsperiodeList.size, actual.size)
        for (index in opptjeningsperiodeList.indices) {
            assertEquals(opptjeningsperiodeList[index], actual[index])
        }
    }
}