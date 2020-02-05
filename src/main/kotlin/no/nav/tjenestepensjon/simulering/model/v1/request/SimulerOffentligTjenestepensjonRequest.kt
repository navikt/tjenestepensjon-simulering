package no.nav.tjenestepensjon.simulering.model.v1.request

import com.fasterxml.jackson.annotation.JsonCreator
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.TpForhold
import java.time.LocalDate

data class SimulerOffentligTjenestepensjonRequest @JsonCreator constructor(
        var fnr: FNR,
        var tpnr: String,
        var tssEksternId: String,
        var forsteUttakDato: LocalDate,
        var uttaksgrad: Int? = null,
        var heltUttakDato: LocalDate? = null,
        var stillingsprosentOffHeltUttak: Int? = null,
        var stillingsprosentOffGradertUttak: Int? = null,
        var inntektForUttak: Int? = null,
        var inntektUnderGradertUttak: Int? = null,
        var inntektEtterHeltUttak: Int? = null,
        var antallArInntektEtterHeltUttak: Int? = null,
        var sivilstandKode: String,
        var sprak: String = "norsk",
        var simulertAFPOffentlig: Int? = null,
        var simulertAFPPrivat: SimulertAFPPrivat? = null,
        var simulertAP2011: SimulertAP2011,
        var tpForholdListe: List<TpForhold> = emptyList()
)