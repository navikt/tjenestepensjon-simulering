package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.ping.Pingable
import no.nav.tjenestepensjon.simulering.service.FeatureToggleService
import no.nav.tjenestepensjon.simulering.service.FeatureToggleService.Companion.PEN_715_SIMULER_SPK
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjonMedMaanedsUtbetalinger
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TjenestepensjonSimuleringException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TomSimuleringFraTpOrdningException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TpUtil
import org.springframework.stereotype.Service

@Service
class SPKTjenestepensjonService(private val client: SPKTjenestepensjonClient, private val featureToggleService: FeatureToggleService) : Pingable {
    private val log = KotlinLogging.logger {}
    private val TP_ORDNING = "spk"


    fun simuler(request: SimulerTjenestepensjonRequestDto, tpNummer: String): Result<SimulertTjenestepensjonMedMaanedsUtbetalinger> {
        if (!featureToggleService.isEnabled(PEN_715_SIMULER_SPK)) {
            return loggOgReturn()
        }

        return client.simuler(request, tpNummer)
            .fold(
                onSuccess = {
                    if (it.utbetalingsperioder.isEmpty())
                        Result.failure(TomSimuleringFraTpOrdningException(TP_ORDNING))
                    else
                        Result.success(
                            SimulertTjenestepensjonMedMaanedsUtbetalinger(
                                tpLeverandoer = SPKMapper.PROVIDER_FULLT_NAVN,
                                tpNummer = tpNummer,
                                ordningsListe = it.ordningsListe,
                                utbetalingsperioder = TpUtil.grupperMedDatoFra(fjerneAfp(it.utbetalingsperioder), request.foedselsdato),
                                betingetTjenestepensjonErInkludert = it.betingetTjenestepensjonErInkludert,
                                serviceData = it.serviceData
                            )
                        )
                },
                onFailure = { Result.failure(it) }
            )
    }

    private fun loggOgReturn(): Result<SimulertTjenestepensjonMedMaanedsUtbetalinger> {
        val message = "Simulering av tjenestepensjon hos SPK er slått av"
        log.warn { message }
        return Result.failure(TjenestepensjonSimuleringException(message))
    }

    fun fjerneAfp(utbetalingsliste: List<Utbetalingsperiode>) : List<Utbetalingsperiode> {
        val afp = utbetalingsliste.filter { it.ytelseType == "OAFP" }
        if (afp.isNotEmpty()){
            log.info { "AFP fra SPK: $afp" }
        }
        return utbetalingsliste.filter { it.ytelseType != "OAFP" }
    }

    override fun ping() = client.ping()
}