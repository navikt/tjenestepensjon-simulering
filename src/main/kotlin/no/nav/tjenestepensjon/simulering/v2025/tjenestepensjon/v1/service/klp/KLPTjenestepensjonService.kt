package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.ping.Pingable
import no.nav.tjenestepensjon.simulering.service.FeatureToggleService
import no.nav.tjenestepensjon.simulering.service.FeatureToggleService.Companion.SIMULER_KLP
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjonMedMaanedsUtbetalinger
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.IkkeSisteOrdningException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TomSimuleringFraTpOrdningException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TpOrdningStoettesIkkeException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TpUtil
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TpUtil.redact
import org.springframework.stereotype.Service

@Service
class KLPTjenestepensjonService(private val client: KLPTjenestepensjonClient, private val featureToggleService: FeatureToggleService) : Pingable {
    private val log = KotlinLogging.logger {}
    private val TP_ORDNING = "klp"

    fun simuler(request: SimulerTjenestepensjonRequestDto, tpNummer: String): Result<SimulertTjenestepensjonMedMaanedsUtbetalinger> {
        if (!featureToggleService.isEnabled(SIMULER_KLP)) {
            return loggOgReturn()
        }

        return client.simuler(request, tpNummer)
            .fold(
                onSuccess = {
                    if (!it.erSisteOrdning){
                        Result.failure(IkkeSisteOrdningException(TP_ORDNING))
                    }
                    else if (it.utbetalingsperioder.isEmpty())
                        Result.failure(TomSimuleringFraTpOrdningException(TP_ORDNING))
                    else
                        Result.success(
                            SimulertTjenestepensjonMedMaanedsUtbetalinger(
                                tpLeverandoer = KLPMapper.PROVIDER_FULLT_NAVN,
                                tpNummer = tpNummer,
                                ordningsListe = it.ordningsListe,
                                utbetalingsperioder = TpUtil.grupperMedDatoFra(
                                    eksluderYtelser(it.utbetalingsperioder),
                                    request.foedselsdato
                                ),
                                aarsakIngenUtbetaling = it.aarsakIngenUtbetaling,
                                betingetTjenestepensjonErInkludert = false,
                                serviceData = it.serviceData
                            ))
                },
                onFailure = { Result.failure(it) }
            ).also { it.onSuccess {
                log.info { "tjenestepensjonsrequest til KLP: ${redact(it.serviceData.toString())}" } }
            }

    }

  private fun eksluderYtelser(utbetalingsperiode: List<Utbetalingsperiode>): List<Utbetalingsperiode> {
    return utbetalingsperiode.filter { it.ytelseType !in setOf("OAFP", "BTP") }
}

    private fun loggOgReturn(): Result<SimulertTjenestepensjonMedMaanedsUtbetalinger> {
        val message = "Simulering av tjenestepensjon hos KLP er slått av"
        log.warn { message }
        return Result.failure(TpOrdningStoettesIkkeException(TP_ORDNING))
    }

    override fun ping() = client.ping()
}