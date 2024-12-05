package no.nav.tjenestepensjon.simulering.sporingslogg

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

object SporingDtoMapper {
    fun toDto(sporingsrapport: Sporingsrapport): SporingDto {
        return SporingDto(
            person = sporingsrapport.ident,
            mottaker = sporingsrapport.organisasjonsnummer,
            tema = "PEK",
            behandlingsGrunnlag = "B353",
            uthentingsTidspunkt = LocalDateTime.now().format(ISO_LOCAL_DATE_TIME),
            dataForespoersel = sporingsrapport.dataSendtIRequest,
            leverteData = "(no data)"
        )
    }
}