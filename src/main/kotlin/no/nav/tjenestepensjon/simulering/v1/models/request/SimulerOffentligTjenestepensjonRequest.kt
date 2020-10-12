package no.nav.tjenestepensjon.simulering.v1.models.request

import no.nav.tjenestepensjon.simulering.v1.models.domain.*
import no.nav.tjenestepensjon.simulering.v1.TPOrdningStillingsprosentMap
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import java.time.LocalDate
import java.time.temporal.ChronoUnit.YEARS

data class SimulerOffentligTjenestepensjonRequest(
        val fnr: FNR,
        val tpnr: String,
        val tssEksternId: String,
        val forsteUttakDato: LocalDate,
        val uttaksgrad: Int? = null,
        val heltUttakDato: LocalDate? = null,
        val stillingsprosentOffHeltUttak: Int? = null,
        val stillingsprosentOffGradertUttak: Int? = null,
        val inntektForUttak: Int? = null,
        val inntektUnderGradertUttak: Int? = null,
        val inntektEtterHeltUttak: Int? = null,
        val antallArInntektEtterHeltUttak: Int? = null,
        val sivilstandKode: String,
        val sprak: String? = "norsk",
        val simulertAFPOffentlig: Int? = null,
        val simulertAFPPrivat: SimulertAFPPrivat? = null,
        val simulertAP2011: SimulertAP2011,
        val tpForholdListe: List<TpForhold> = emptyList()
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
            val latestInntekt = inntektList.maxOrNull() ?: return 0
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