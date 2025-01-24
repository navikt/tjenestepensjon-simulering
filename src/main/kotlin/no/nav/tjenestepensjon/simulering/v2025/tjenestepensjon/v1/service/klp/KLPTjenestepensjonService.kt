package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.common.AlderUtil.bestemUttaksalderVedDato
import no.nav.tjenestepensjon.simulering.ping.Pingable
import no.nav.tjenestepensjon.simulering.service.FeatureToggleService
import no.nav.tjenestepensjon.simulering.service.FeatureToggleService.Companion.SIMULER_KLP
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Maanedsutbetaling
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Ordning
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjonMedMaanedsUtbetalinger
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TomSimuleringFraTpOrdningException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TpOrdningStoettesIkkeException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class KLPTjenestepensjonService(@Value("\${spring.profiles.active:}") private val activeProfiles: String, private val client: KLPTjenestepensjonClient, private val featureToggleService: FeatureToggleService) : Pingable {
    private val log = KotlinLogging.logger {}
    private val TP_ORDNING = "klp"

    fun simuler(request: SimulerTjenestepensjonRequestDto, tpNummer: String): Result<SimulertTjenestepensjonMedMaanedsUtbetalinger> {
        if (!featureToggleService.isEnabled(SIMULER_KLP)) {
            return loggOgReturn()
        }

        if (activeProfiles.contains("prod-gcp")) {
            return simulerv2(request, tpNummer)
        }

        val maanedsutbetalingMock = Maanedsutbetaling(
            fraOgMedDato = request.uttaksdato,
            fraOgMedAlder = bestemUttaksalderVedDato(fodselsdato = request.foedselsdato, date = request.uttaksdato),
            maanedsBeloep = 5000,
        )

        val maanedsutbetalingMock2 = Maanedsutbetaling(
            fraOgMedDato = request.uttaksdato.plusYears(5),
            fraOgMedAlder = bestemUttaksalderVedDato(fodselsdato = request.foedselsdato, date = request.uttaksdato.plusYears(5)),
            maanedsBeloep = 6000,
        )

        val klpResponseMock = SimulertTjenestepensjonMedMaanedsUtbetalinger(
            tpLeverandoer = "Kommunal Landspensjonskasse",
            ordningsListe = arrayListOf(Ordning("3100")),
            utbetalingsperioder = arrayListOf(maanedsutbetalingMock, maanedsutbetalingMock2),
            aarsakIngenUtbetaling = emptyList(),
            betingetTjenestepensjonErInkludert = false,
            serviceData = emptyList()
        )

        return Result.success(klpResponseMock)
    }

    private fun simulerv2(request: SimulerTjenestepensjonRequestDto, tpNummer: String): Result<SimulertTjenestepensjonMedMaanedsUtbetalinger> {
        return client.simuler(request, tpNummer)
            .fold(
                onSuccess = {
                    if (it.utbetalingsperioder.isEmpty())
                        Result.failure(TomSimuleringFraTpOrdningException(TP_ORDNING))
                    else
                        Result.success(
                            SimulertTjenestepensjonMedMaanedsUtbetalinger(
                                tpLeverandoer = KLPMapper.PROVIDER_FULLT_NAVN,
                                ordningsListe = emptyList(),
                                utbetalingsperioder = emptyList(),
                                aarsakIngenUtbetaling = emptyList(),
                                betingetTjenestepensjonErInkludert = false,
                                serviceData = it.serviceData
                            ))
                },
                onFailure = { Result.failure(it) }
            ).also { it.onSuccess {
                log.info { "${it.serviceData}" } }
            }

    }


        private fun loggOgReturn(): Result<SimulertTjenestepensjonMedMaanedsUtbetalinger> {
        val message = "Simulering av tjenestepensjon hos KLP er slått av"
        log.warn { message }
        return Result.failure(TpOrdningStoettesIkkeException(TP_ORDNING))
    }

    override fun ping() = client.ping()


}