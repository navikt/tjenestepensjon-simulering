package no.nav.tjenestepensjon.simulering.v2.models.request

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.v2.models.domain.SivilstandCodeEnum

data class SimulerPensjonRequest(
        var fnr: FNR,
        var fodselsdato:String,
        var sisteTpnr:String,
        var sprak:String,
        var simulertAfpOffentlig: SimulertAfpOffentlig,
        var simulertAfpPrivat: SimulertAfpPrivat,
        var sivilstandCode: SivilstandCodeEnum,
        var inntektListe: List<Inntekt>,
        var pensjonsbeholdningsperiodeListe: List<Pensjonsbeholdningsperiode>,
        var simuleringsperiodeListe: List<Simuleringsperiode>,
        var simuleringsdataListe: List<Simuleringsdata>,
        var tpForholdListe: List<TpForhold>
)