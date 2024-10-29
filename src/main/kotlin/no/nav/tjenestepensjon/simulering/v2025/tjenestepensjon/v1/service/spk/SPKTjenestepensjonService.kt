package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk

import no.nav.tjenestepensjon.simulering.ping.Pingable
import no.nav.tjenestepensjon.simulering.v2025.afp.v1.AlderForDelingstallBeregner.bestemAlderVedDato
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Maanedsutbetaling
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjonMedMaanedsUtbetalinger
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SPKTjenestepensjonService(private val client: SPKTjenestepensjonClient) : Pingable {

    fun simuler(request: SimulerTjenestepensjonRequestDto): Result<SimulertTjenestepensjonMedMaanedsUtbetalinger> {
        return client.simuler(request)
            .fold(
                onSuccess = {
                    Result.success(
                        SimulertTjenestepensjonMedMaanedsUtbetalinger(
                            tpLeverandoer = it.tpLeverandoer,
                            ordningsListe = it.ordningsListe,
                            utbetalingsperioder = grupperMedDatoFra(it.utbetalingsperioder, request.foedselsdato)
                        )
                    )
                },
                onFailure = { Result.failure(it) }
            )
    }

    fun grupperMedDatoFra(utbetalingsliste: List<Utbetalingsperiode>, foedselsdato: LocalDate): List<Maanedsutbetaling> {
        return utbetalingsliste
            .groupBy { it.fom }
            .map { (datoFra, ytelser) ->
                val totalMaanedsBelop = ytelser.sumOf { it.maanedligBelop }
                Maanedsutbetaling(datoFra, bestemAlderVedDato(foedselsdato, datoFra), totalMaanedsBelop)
            }
            .sortedBy { it.fraOgMedDato }
    }

    override fun ping() = client.ping()
}