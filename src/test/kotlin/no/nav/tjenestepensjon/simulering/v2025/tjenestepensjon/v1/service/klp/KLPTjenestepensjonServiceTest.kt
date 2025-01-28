package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp

import no.nav.tjenestepensjon.simulering.service.FeatureToggleService
import no.nav.tjenestepensjon.simulering.service.FeatureToggleService.Companion.SIMULER_KLP
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Ordning
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjonMedMaanedsUtbetalinger
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TpOrdningStoettesIkkeException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025ServiceTest.Companion.dummyRequest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDate

@SpringBootTest
class KLPTjenestepensjonServiceTest {

    @MockitoBean
    private lateinit var featureToggleService: FeatureToggleService

    @Autowired
    private lateinit var klpTjenestepensjonService: KLPTjenestepensjonService


@Test
fun `simulering skal ikke skje naar feature toggle er av`() {
    val req = dummyRequest("1963-02-05")
    `when`(featureToggleService.isEnabled(SIMULER_KLP)).thenReturn(false)

    val res : Result<SimulertTjenestepensjonMedMaanedsUtbetalinger> = klpTjenestepensjonService.simuler(req,"4080")

    val exception = res.exceptionOrNull()
    assertTrue(res.isFailure)
    assertTrue(exception is TpOrdningStoettesIkkeException)
}

    fun dummyResult(inkluderBTP: Boolean = false) : Result<SimulertTjenestepensjon> {
        return Result.success(SimulertTjenestepensjon(
            tpLeverandoer = "spk",
            ordningsListe = listOf(Ordning("3010")),
            utbetalingsperioder = listOf(
                Utbetalingsperiode(
                    fom = LocalDate.parse("2026-03-01"),
                    maanedligBelop = 3000,
                    ytelseType = "SAERALDERSPAASLAG"
                ),
                Utbetalingsperiode(
                    fom = LocalDate.parse("2026-03-01"),
                    maanedligBelop = 4000,
                    ytelseType = "OT6370"
                ),
                Utbetalingsperiode(
                    fom = LocalDate.parse("2026-01-01"),
                    maanedligBelop = 1000,
                    ytelseType = "PAASLAG"
                ),
                Utbetalingsperiode(
                    fom = LocalDate.parse("2026-01-01"),
                    maanedligBelop = 2000,
                    ytelseType = "APOF2020"
                ),Utbetalingsperiode(
                    fom = LocalDate.parse("2026-10-01"),
                    maanedligBelop = 2000,
                    ytelseType = "BTP"
                ),
            ),
            betingetTjenestepensjonErInkludert = inkluderBTP
        ))
    }
}