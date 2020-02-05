package no.nav.tjenestepensjon.simulering.mapper

import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.Simuleringsperiode
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulertAP2011

object SimulertAP2011Mapper {
    /**
     * Mapping function for scenario fullt uttak (forsteUttak == 100%).
     * If the user is older than 67 at the time of fullt uttak, the values from the forsteUttak is also set on [SimulertAP2011.simulertHeltUttakEtter67Ar].
     * Prior to the transition to the new kap.20 information-model, PEN would provide this value to the ESB, but this information is "lost" in the new model.
     *
     * @param forsteUttak [Simuleringsperiode] for forsteuttak
     * @param fnr fodselsnr
     * @return [SimulertAP2011]
     * @see "TPEN630 SimulerTP and FPEN032 hentBeregningsinformasjonForTP in PEN for further reference"
     */
    fun mapFulltUttak(forsteUttak: Simuleringsperiode, fnr: FNR): SimulertAP2011 {
        return SimulertAP2011(
                simulertForsteuttak = SimuleringsdataMapper.mapToSimuleringsdata(forsteUttak),
                simulertHeltUttakEtter67Ar = forsteUttak.takeIf { isUttakEtter67Ar(it, fnr) }
                        ?.let(SimuleringsdataMapper::mapToSimuleringsdata)
        )
    }

    /**
     * Mapping function for scenario gradert uttak (forstUttak < 100%)
     * Unlike the scenario for fullt uttak, PEN will provide two periods for gradert uttak. In this case, the value of [SimulertAP2011.simulertHeltUttakEtter67Ar]
     * should be set without checking the age (this is handled by PEN).
     *
     * @param forsteUttak [Simuleringsperiode] for forsteuttak
     * @param heltUttak [Simuleringsperiode] for helt uttak
     * @return [SimulertAP2011]
     * @see "TPEN630 SimulerTP and FPEN032 hentBeregningsinformasjonForTP in PEN for further reference"
     */
    fun mapGradertUttak(forsteUttak: Simuleringsperiode, heltUttak: Simuleringsperiode) =
            SimulertAP2011(
                    simulertForsteuttak = SimuleringsdataMapper.mapToSimuleringsdata(forsteUttak),
                    simulertHeltUttakEtter67Ar = SimuleringsdataMapper.mapToSimuleringsdata(heltUttak)
            )

    private fun isUttakEtter67Ar(simuleringsperiode: Simuleringsperiode, fnr: FNR) =
            simuleringsperiode.datoFom >= fnr.birthDate.plusYears(67)
}