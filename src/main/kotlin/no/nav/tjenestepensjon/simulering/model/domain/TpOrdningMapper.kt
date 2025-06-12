package no.nav.tjenestepensjon.simulering.model.domain

object TpOrdningMapper {

    fun mapTilTpOrdningDto(dto: TpOrdningMedDato): TpOrdningDto {
        return TpOrdningDto(
            navn = dto.navn,
            tpNr = dto.tpNr,
            orgNr = "",
            alias = emptyList()
        )
    }

    fun mapTilTpOrdningFullDto(dto: TpOrdningMedDato, idDto: TPOrdningIdDto): TpOrdningFullDto {
        return TpOrdningFullDto(
            navn = dto.navn,
            tpNr = dto.tpNr,
            datoSistOpptjening = dto.datoSistOpptjening?.toString(),
            tssId = idDto.tssId,
        )
    }
}