package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.response

data class SimulerTjenestepensjonResponseDto(
    val simuleringsResultatStatus: SimuleringsResultatStatusDto,
    val simuleringsResultatDto: SimuleringsResultatDto? = null,
){
    constructor(simuleringsResultatTypeDto: SimuleringsResultatTypeDto, feilmelding: String) : this(
        SimuleringsResultatStatusDto(simuleringsResultatTypeDto, feilmelding)
    )
}

data class SimuleringsResultatStatusDto(
    val simuleringsResultatStatus: SimuleringsResultatTypeDto,
    val feilmelding: String? = null,
)

enum class SimuleringsResultatTypeDto { SUCCESS, ERROR }

data class SimuleringsResultatDto(
    val utbetalingsperioder: List<UtbetalingsperiodeDto>,
    val aarsakIngenUtbetaling: List<String>,
)