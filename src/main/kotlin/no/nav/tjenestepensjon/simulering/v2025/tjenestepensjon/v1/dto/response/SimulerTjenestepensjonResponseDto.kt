package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.response

data class SimulerTjenestepensjonResponseDto(
    val simuleringsResultatStatus: SimuleringsResultatStatusDto,
    val simuleringsResultat: SimuleringsResultatDto? = null,
){
    constructor(resultatTypeDto: ResultatTypeDto, feilmelding: String?) : this(
        SimuleringsResultatStatusDto(resultatTypeDto, feilmelding)
    )
}

data class SimuleringsResultatStatusDto(
    val resultatType: ResultatTypeDto,
    val feilmelding: String? = null,
)

enum class ResultatTypeDto { SUCCESS, ERROR }

data class SimuleringsResultatDto(
    val tpLeverandoer: String,
    val utbetalingsperioder: List<UtbetalingPerAar>,
)

data class UtbetalingPerAar(
    val aar: Int,
    val beloep: Int,
)