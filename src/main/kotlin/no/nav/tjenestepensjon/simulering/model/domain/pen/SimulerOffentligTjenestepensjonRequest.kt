package no.nav.tjenestepensjon.simulering.model.domain.pen

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.tjenestepensjon.simulering.v2.models.domain.SivilstandCodeEnum

@JsonIgnoreProperties(ignoreUnknown = true)
data class SimulerOffentligTjenestepensjonRequest(
    val fnr: String,
    val fodselsdato: String,
    val sisteTpnr: String?,
    val sprak: String?,
    val simulertAFPOffentlig: SimulertAFPOffentligDto?,
    val simulertAFPPrivat: SimulertAFPPrivatDto?,
    val sivilstandkode: SivilstandCodeEnum,
    val inntektListe: List<InntektDto> = emptyList(),
    val pensjonsbeholdningsperiodeListe: List<PensjonsbeholdningsperiodeDto> = emptyList(),
    val simuleringsperiodeListe: List<SimuleringsperiodeDto> = emptyList(),
    val simuleringsdataListe: List<SimuleringsdataDto> = emptyList(),
    val tpForholdListe: List<TpForholdDto> = emptyList(),
){

    override fun toString(): String {
        return "SimulerOffentligTjenestepensjonRequest(fodselsdato='$fodselsdato', sisteTpnr=$sisteTpnr, sprak=$sprak, simulertAFPOffentlig=$simulertAFPOffentlig, simulertAFPPrivat=$simulertAFPPrivat, sivilstandkode=$sivilstandkode, inntektListe=$inntektListe, pensjonsbeholdningsperiodeListe=$pensjonsbeholdningsperiodeListe, simuleringsperiodeListe=$simuleringsperiodeListe, simuleringsdataListe=$simuleringsdataListe, tpForholdListe=$tpForholdListe)"
    }
}