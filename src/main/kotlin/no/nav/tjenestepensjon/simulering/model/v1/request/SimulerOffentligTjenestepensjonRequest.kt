package no.nav.tjenestepensjon.simulering.model.v1.request

import com.fasterxml.jackson.annotation.JsonCreator
import no.nav.tjenestepensjon.simulering.model.v1.domain.*
import no.nav.tjenestepensjon.simulering.util.TPOrdningStillingsprosentMap
import java.time.LocalDate
import java.time.temporal.ChronoUnit.YEARS

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
) {
    constructor(
            simulerPensjonRequest: SimulerPensjonRequest,
            tpOrdning: TPOrdning,
            tpOrdningStillingsprosentMap: TPOrdningStillingsprosentMap,
            forsteUttak: Simuleringsperiode,
            heltUttak: Simuleringsperiode
    ) : this(
            fnr = simulerPensjonRequest.fnr,
            sprak = simulerPensjonRequest.sprak,
            tpnr = tpOrdning.tpId,
            uttaksgrad = forsteUttak.utg,
            tssEksternId = tpOrdning.tssId,
            sivilstandKode = simulerPensjonRequest.sivilstandkode,
            heltUttakDato = heltUttak.datoFom,
            forsteUttakDato = forsteUttak.datoFom,
            simulertAFPPrivat = simulerPensjonRequest.simulertAFPPrivat,
            simulertAFPOffentlig = simulerPensjonRequest.simulertAFPOffentlig,
            stillingsprosentOffHeltUttak = heltUttak.stillingsprosentOffentlig,
            inntektForUttak = findInntektForUttak(simulerPensjonRequest.inntekter, forsteUttak.datoFom),
            stillingsprosentOffGradertUttak = forsteUttak.stillingsprosentOffentlig,
            inntektEtterHeltUttak = findInntektSumOnDate(simulerPensjonRequest.inntekter, heltUttak.datoFom),
            simulertAP2011 = SimulertAP2011(forsteUttak, heltUttak),
            inntektUnderGradertUttak = findInntektSumOnDate(simulerPensjonRequest.inntekter, forsteUttak.datoFom),
            antallArInntektEtterHeltUttak = findAntallArInntektEtterHeltUttak(simulerPensjonRequest.inntekter, heltUttak.datoFom),
            tpForholdListe = tpOrdningStillingsprosentMap.map { TpForhold(it.key, it.value) }
    )

    constructor(
            simulerPensjonRequest: SimulerPensjonRequest,
            tpOrdning: TPOrdning,
            tpOrdningStillingsprosentMap: TPOrdningStillingsprosentMap,
            forsteUttak: Simuleringsperiode
    ) : this(
            fnr = simulerPensjonRequest.fnr,
            sprak = simulerPensjonRequest.sprak,
            tpnr = tpOrdning.tpId,
            uttaksgrad = forsteUttak.utg,
            tssEksternId = tpOrdning.tssId,
            sivilstandKode = simulerPensjonRequest.sivilstandkode,
            forsteUttakDato = forsteUttak.datoFom,
            simulertAFPPrivat = simulerPensjonRequest.simulertAFPPrivat,
            simulertAFPOffentlig = simulerPensjonRequest.simulertAFPOffentlig,
            stillingsprosentOffHeltUttak = forsteUttak.stillingsprosentOffentlig,
            simulertAP2011 = SimulertAP2011(forsteUttak, simulerPensjonRequest.fnr),
            inntektForUttak = findInntektForUttak(simulerPensjonRequest.inntekter, forsteUttak.datoFom),
            inntektEtterHeltUttak = findInntektSumOnDate(simulerPensjonRequest.inntekter, forsteUttak.datoFom),
            antallArInntektEtterHeltUttak = findAntallArInntektEtterHeltUttak(simulerPensjonRequest.inntekter, forsteUttak.datoFom),
            tpForholdListe = tpOrdningStillingsprosentMap.map { TpForhold(it.key, it.value) }
    )

    companion object {
        private fun findInntektOnDate(inntektList: List<Inntekt>, date: LocalDate) =
                inntektList.firstOrNull { inntekt: Inntekt -> inntekt.datoFom == date }

        private fun findInntektSumOnDate(inntektList: List<Inntekt>, date: LocalDate) =
                findInntektOnDate(inntektList, date)?.inntekt?.toInt()


        fun findAntallArInntektEtterHeltUttak(inntektList: List<Inntekt>, uttaksDato: LocalDate): Int {
            val inntektAtUttaksDato = findInntektOnDate(inntektList, uttaksDato) ?: return 0
            val latestInntekt = inntektList.max() ?: return 0
            return YEARS.between(latestInntekt.datoFom, inntektAtUttaksDato.datoFom).toInt()
        }

        fun findInntektForUttak(inntektList: List<Inntekt>, uttaksDato: LocalDate) =
                findInntektOnDate(inntektList, uttaksDato)
                        ?.let {
                            inntektList.sorted().reversed()
                                    .dropWhile { inntekt: Inntekt -> it != inntekt }
                                    .getOrNull(1)
                        }?.inntekt?.toInt()
    }
}