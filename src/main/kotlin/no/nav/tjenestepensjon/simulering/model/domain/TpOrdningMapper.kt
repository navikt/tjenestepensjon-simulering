package no.nav.tjenestepensjon.simulering.model.domain

object TpOrdningMapper {

    fun mapTilTpOrdningFullDto(dto: TpOrdningMedDato, idDto: TPOrdningIdDto): TpOrdningFullDto {
        return TpOrdningFullDto(
            navn = dto.navn,
            tpNr = dto.tpNr,
            datoSistOpptjening = dto.datoSistOpptjening,
            tssId = idDto.tssId,
        )
    }
}