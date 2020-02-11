package no.nav.tjenestepensjon.simulering.model.v1.request

import com.fasterxml.jackson.annotation.JsonCreator
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.Simuleringsperiode

data class SimulertAP2011 @JsonCreator constructor(
        val simulertForsteuttak: Simuleringsdata,
        val simulertHeltUttakEtter67Ar: Simuleringsdata? = null
) {

    constructor(forsteUttak: Simuleringsperiode, fnr: FNR) : this(
            simulertForsteuttak = Simuleringsdata(forsteUttak),
            simulertHeltUttakEtter67Ar = forsteUttak.takeIf { isUttakEtter67Ar(it, fnr) }
                    ?.let(::Simuleringsdata)
    )

    constructor(forsteUttak: Simuleringsperiode, heltUttak: Simuleringsperiode) : this(
            simulertForsteuttak = Simuleringsdata(forsteUttak),
            simulertHeltUttakEtter67Ar = Simuleringsdata(heltUttak)
    )

    companion object {
        private fun isUttakEtter67Ar(simuleringsperiode: Simuleringsperiode, fnr: FNR) =
                simuleringsperiode.datoFom >= fnr.birthDate.plusYears(67)
    }
}