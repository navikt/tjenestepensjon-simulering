package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp

import no.nav.tjenestepensjon.simulering.common.AlderUtil.bestemUttaksalderVedDato
import no.nav.tjenestepensjon.simulering.ping.Pingable
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Maanedsutbetaling
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Ordning
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjonMedMaanedsUtbetalinger
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import org.springframework.stereotype.Service

@Service
class KLPTjenestepensjonService(private val client: KLPTjenestepensjonClient) : Pingable {

    fun simuler(request: SimulerTjenestepensjonRequestDto): Result<SimulertTjenestepensjonMedMaanedsUtbetalinger> {

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
            tpLeverandoer = "Kommunal landspensjonskasse",
            ordningsListe = arrayListOf(Ordning("3100")),
            utbetalingsperioder = arrayListOf(maanedsutbetalingMock, maanedsutbetalingMock2),
            aarsakIngenUtbetaling = emptyList(),
            betingetTjenestepensjonErInkludert = false,
            serviceData = emptyList()
        )

        return Result.success(klpResponseMock)
    }

    override fun ping() = client.ping()


}