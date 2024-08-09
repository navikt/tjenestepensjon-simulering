package no.nav.tjenestepensjon.simulering.v2

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl.REST
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.v1.service.StillingsprosentResponse
import no.nav.tjenestepensjon.simulering.v2.exceptions.OpptjeningsperiodeCallableException
import no.nav.tjenestepensjon.simulering.v2.models.domain.Opptjeningsperiode
import no.nav.tjenestepensjon.simulering.v2.models.domain.SivilstandCodeEnum
import no.nav.tjenestepensjon.simulering.v2.models.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequestV2
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v2.rest.RestClient
import no.nav.tjenestepensjon.simulering.v2.service.OpptjeningsperiodeResponse
import no.nav.tjenestepensjon.simulering.v2.service.OpptjeningsperiodeService
import no.nav.tjenestepensjon.simulering.v2.service.SimuleringServiceV2
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate.now
import java.util.concurrent.ExecutionException
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
internal class SimpleSimuleringServiceTest {

    @Mock
    private lateinit var opptjeningsperiodeService: OpptjeningsperiodeService

    @Mock
    private lateinit var restClient: RestClient

    @Mock
    @Suppress("unused")
    private lateinit var metrics: AppMetrics

    @InjectMocks
    private lateinit var simuleringService: SimuleringServiceV2

    private lateinit var request: SimulerPensjonRequestV2

    @BeforeEach
    fun beforeEach() {
        request = SimulerPensjonRequestV2(
            fnr = FNR("01011234567"),
            fodselsdato = "1968-01-01",
            sisteTpnr = "12345",
            sivilstandkode = SivilstandCodeEnum.GIFT,
            inntektListe = emptyList(),
            simuleringsperiodeListe = emptyList(),
            simuleringsdataListe = emptyList(),
            tpForholdListe = emptyList()
        )
    }

    @Test
    fun `Should return response object`() {
        val map = mapOf(TPOrdningIdDto("tssInkluder", "tpInkluder") to emptyList<Opptjeningsperiode>())
        val exceptions = listOf(
            ExecutionException(
                OpptjeningsperiodeCallableException(
                    "msg", Throwable(), TPOrdningIdDto("tssUtelatt", "tpUtelatt")
                )
            )
        )
        val opptjeningsperiodeResponse = OpptjeningsperiodeResponse(map, exceptions)

        `when`(opptjeningsperiodeService.getOpptjeningsperiodeListe(anyNonNull())).thenReturn(
            opptjeningsperiodeResponse
        )

        val s1 = SimulerOffentligTjenestepensjonResponse(
            tpnr = "feil", navnOrdning = "feil", utbetalingsperiodeListe = listOf(
                Utbetalingsperiode(
                    uttaksgrad = 0, arligUtbetaling = 0.0, datoTom = now(), datoFom = now(), ytelsekode = "avdod"
                )
            )
        )
        val tpOrdningIdDto = TPOrdningIdDto("fake", "faker")
        val tpLeverandor = TpLeverandor("fake", REST, "faker", "faker")
        `when`(restClient.getResponse(anyNonNull(), anyNonNull(), anyNonNull())).thenReturn(s1)

        val response = simuleringService.simulerOffentligTjenestepensjon(
            request, StillingsprosentResponse(emptyMap(), emptyList()), tpOrdningIdDto, tpLeverandor
        )

        assertNotNull(response)
    }

    @Test
    fun `Should increment metrics`() {
        val map = mapOf(TPOrdningIdDto("tssInkluder", "tpInkluder") to emptyList<Opptjeningsperiode>())
        val opptjeningsperiodeResponse = OpptjeningsperiodeResponse(map, listOf())

        `when`(opptjeningsperiodeService.getOpptjeningsperiodeListe(anyNonNull())).thenReturn(opptjeningsperiodeResponse)

        val s1 = SimulerOffentligTjenestepensjonResponse(
            utbetalingsperiodeListe = listOf(), tpnr = "", navnOrdning = ""
        )

        val tpOrdningIdDto = TPOrdningIdDto("fake", "faker")
        val tpLeverandor = TpLeverandor("fake", REST, "faker", "faker")

        `when`(restClient.getResponse(anyNonNull(), anyNonNull(), anyNonNull())).thenReturn(s1)
        assertNotNull(
            simuleringService.simulerOffentligTjenestepensjon(
                request, StillingsprosentResponse(emptyMap(), emptyList()), tpOrdningIdDto, tpLeverandor
            )
        )
    }
}
