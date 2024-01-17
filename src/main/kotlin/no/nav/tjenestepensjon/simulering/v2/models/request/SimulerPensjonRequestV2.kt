package no.nav.tjenestepensjon.simulering.v2.models.request

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.Pensjonsbeholdningsperiode
import no.nav.tjenestepensjon.simulering.v2.models.domain.SivilstandCodeEnum

data class SimulerPensjonRequestV2(
    var fnr: FNR,
    var fodselsdato: String,
    var sisteTpnr: String,
    var sprak: String? = null,
    var simulertAFPOffentlig: SimulertAFPOffentlig? = null,
    var simulertAFPPrivat: SimulertAFPPrivat? = null,
    var sivilstandkode: SivilstandCodeEnum,
    var inntektListe: List<Inntekt>,
    var pensjonsbeholdningsperiodeListe: List<Pensjonsbeholdningsperiode> = emptyList(),
    var simuleringsperiodeListe: List<Simuleringsperiode>,
    var simuleringsdataListe: List<Simuleringsdata>,
    var tpForholdListe: List<TpForhold> = emptyList()
)
