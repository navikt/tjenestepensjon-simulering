package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.common.AlderUtil.bestemAlderVedDato
import no.nav.tjenestepensjon.simulering.common.AlderUtil.bestemUttaksalderVedDato
import no.nav.tjenestepensjon.simulering.ping.Pingable
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Maanedsutbetaling
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjonMedMaanedsUtbetalinger
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TjenestepensjonSimuleringException
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SPKTjenestepensjonService(private val client: SPKTjenestepensjonClient, private val environment: Environment) : Pingable {
    private val log = KotlinLogging.logger {}

    fun simuler(request: SimulerTjenestepensjonRequestDto): Result<SimulertTjenestepensjonMedMaanedsUtbetalinger> {
        if (environment.activeProfiles.contains("prod-gcp")) {
            return Result.failure(TjenestepensjonSimuleringException("Simulering av tjenestepensjon hos SPK er ikke tilgjengelig i produksjon"))
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