package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.common.AlderUtil.bestemUttaksalderVedDato
import no.nav.tjenestepensjon.simulering.ping.Pingable
import no.nav.tjenestepensjon.simulering.service.FeatureToggleService
import no.nav.tjenestepensjon.simulering.service.FeatureToggleService.Companion.PEN_715_SIMULER_SPK
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Maanedsutbetaling
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjonMedMaanedsUtbetalinger
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TjenestepensjonSimuleringException
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SPKTjenestepensjonService(private val client: SPKTjenestepensjonClient, private val featureToggleService: FeatureToggleService) : Pingable {
    private val log = KotlinLogging.logger {}

    fun simuler(request: SimulerTjenestepensjonRequestDto): Result<SimulertTjenestepensjonMedMaanedsUtbetalinger> {
        if (!featureToggleService.isEnabled(PEN_715_SIMULER_SPK)) {
            return loggOgReturn()
        }

        return client.simuler(request)
            .fold(
                onSuccess = {
                    Result.success(
                        SimulertTjenestepensjonMedMaanedsUtbetalinger(
                            tpLeverandoer = SPKMapper.PROVIDER_FULLT_NAVN,
                            ordningsListe = it.ordningsListe,
                            utbetalingsperioder = grupperMedDatoFra(fjerneAfp(it.utbetalingsperioder), request.foedselsdato),
                            betingetTjenestepensjonErInkludert = it.betingetTjenestepensjonErInkludert,
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

    fun grupperMedDatoFra(utbetalingsliste: List<Utbetalingsperiode>, foedselsdato: LocalDate): List<Maanedsutbetaling> {
        return utbetalingsliste
            .groupBy { it.fom }
            .map { (datoFra, ytelser) ->
                val totalMaanedsBelop = ytelser.sumOf { it.maanedligBelop }
                Maanedsutbetaling(datoFra, bestemUttaksalderVedDato(foedselsdato, datoFra), totalMaanedsBelop)
            }
            .sortedBy { it.fraOgMedDato }
    }

    override fun ping() = client.ping()
}