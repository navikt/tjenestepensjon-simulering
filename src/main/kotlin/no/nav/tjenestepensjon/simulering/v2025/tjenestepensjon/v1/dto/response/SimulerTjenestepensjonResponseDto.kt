package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.response

import no.nav.tjenestepensjon.simulering.model.domain.pen.Alder

data class SimulerTjenestepensjonResponseDto(
    val simuleringsResultatStatus: SimuleringsResultatStatusDto,
    val simuleringsResultat: SimuleringsResultatDto? = null,
    val relevanteTpOrdninger: List<String> = emptyList(),
) {
    constructor(resultatTypeDto: ResultatTypeDto, feilmelding: String?, tpOrdninger: List<String>) : this(
        SimuleringsResultatStatusDto(resultatTypeDto, feilmelding), null, tpOrdninger
    )
}

data class SimuleringsResultatStatusDto(
    val resultatType: ResultatTypeDto,
    val feilmelding: String? = null,
)

enum class ResultatTypeDto {
    SUCCESS,
    BRUKER_ER_IKKE_MEDLEM_HOS_TP_ORDNING,
    TP_ORDNING_ER_IKKE_STOTTET,
    INGEN_UTBETALINGSPERIODER_FRA_TP_ORDNING,
    TEKNISK_FEIL_FRA_TP_ORDNING,
}

data class SimuleringsResultatDto(
    val tpLeverandoer: String,
    val utbetalingsperioder: List<UtbetalingPerAlder>,
    val betingetTjenestepensjonErInkludert: Boolean,
)

data class UtbetalingPerAlder(
    val startAlder: Alder,
    val sluttAlder: Alder?,
    val maanedligBeloep: Int,
)