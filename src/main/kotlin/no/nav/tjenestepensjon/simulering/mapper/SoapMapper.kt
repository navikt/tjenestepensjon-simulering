package no.nav.tjenestepensjon.simulering.mapper

import no.nav.tjenestepensjon.simulering.model.v1.domain.*
import no.nav.tjenestepensjon.simulering.model.v1.request.HentStillingsprosentListeRequest
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerOffentligTjenestepensjonRequest
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerPensjonRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.temporal.ChronoUnit.YEARS

object SoapMapper {
    val LOG: Logger = LoggerFactory.getLogger(SoapMapper.javaClass)
    fun mapStillingsprosentRequest(fnr: FNR, tpOrdning: TPOrdning) = with(tpOrdning) {
        HentStillingsprosentListeRequest(
                fnr = fnr,
                tpnr = tpId,
                tssEksternId = tssId,
                simuleringsKode = "AP"
        )
    }

    fun mapSimulerTjenestepensjonRequest(
            simulerPensjonRequest: SimulerPensjonRequest,
            tpOrdning: TPOrdning,
            tpOrdningStillingsprosentMap: Map<TPOrdning, List<Stillingsprosent>>
    ): SimulerOffentligTjenestepensjonRequest {
        val simuleringsperioder = simulerPensjonRequest.simuleringsperioder

        return (if (simuleringsperioder.size > 1 && simuleringsperioder.min()!!.isGradert())
            createResponseWithGradertForsteUttak(
                    simulerPensjonRequest,
                    tpOrdning,
                    tpOrdningStillingsprosentMap)
        else
            createResponseWithKunForsteUttak(simulerPensjonRequest,
                    tpOrdning,
                    tpOrdningStillingsprosentMap)
                ).also { LOG.info("Mapped IncomingRequest: {} to SimulerOffentligTjenestepensjon: {}", simulerPensjonRequest, it) }
    }

    private fun createResponseWithGradertForsteUttak(
            simulerPensjonRequest: SimulerPensjonRequest,
            tpOrdning: TPOrdning,
            tpOrdningStillingsprosentMap: Map<TPOrdning, List<Stillingsprosent>>
    ) = with(simulerPensjonRequest) {
        val forsteUttak = simuleringsperioder.min()!!
        val heltUttak = simuleringsperioder.max()!!

        SimulerOffentligTjenestepensjonRequest(
                fnr = fnr,
                sprak = sprak,
                tpnr = tpOrdning.tpId,
                uttaksgrad = forsteUttak.utg,
                tssEksternId = tpOrdning.tssId,
                sivilstandKode = sivilstandkode,
                heltUttakDato = heltUttak.datoFom,
                forsteUttakDato = forsteUttak.datoFom,
                simulertAFPPrivat = simulertAFPPrivat,
                simulertAFPOffentlig = simulertAFPOffentlig,
                stillingsprosentOffHeltUttak = heltUttak.stillingsprosentOffentlig,
                inntektForUttak = findInntektForUttak(inntekter, forsteUttak.datoFom),
                stillingsprosentOffGradertUttak = forsteUttak.stillingsprosentOffentlig,
                inntektEtterHeltUttak = findInntektSumOnDate(inntekter, heltUttak.datoFom),
                simulertAP2011 = SimulertAP2011Mapper.mapGradertUttak(forsteUttak, heltUttak),
                inntektUnderGradertUttak = findInntektSumOnDate(inntekter, forsteUttak.datoFom),
                antallArInntektEtterHeltUttak = findAntallArInntektEtterHeltUttak(inntekter, heltUttak.datoFom),
                tpForholdListe = tpOrdningStillingsprosentMap.map { (key, value) -> TpForhold(key.tpId, key.tssId, value) }
        )
    }

    private fun createResponseWithKunForsteUttak(
            simulerPensjonRequest: SimulerPensjonRequest,
            tpOrdning: TPOrdning,
            tpOrdningStillingsprosentMap: Map<TPOrdning, List<Stillingsprosent>>
    ) = with(simulerPensjonRequest) {
        val forsteUttak = simuleringsperioder.min()!!
        SimulerOffentligTjenestepensjonRequest(
                fnr = fnr,
                sprak = sprak,
                tpnr = tpOrdning.tpId,
                uttaksgrad = forsteUttak.utg,
                tssEksternId = tpOrdning.tssId,
                sivilstandKode = sivilstandkode,
                forsteUttakDato = forsteUttak.datoFom,
                simulertAFPPrivat = simulertAFPPrivat,
                simulertAFPOffentlig = simulertAFPOffentlig,
                stillingsprosentOffHeltUttak = forsteUttak.stillingsprosentOffentlig,
                simulertAP2011 = SimulertAP2011Mapper.mapFulltUttak(forsteUttak, fnr),
                inntektForUttak = findInntektForUttak(inntekter, forsteUttak.datoFom),
                inntektEtterHeltUttak = findInntektSumOnDate(inntekter, forsteUttak.datoFom),
                antallArInntektEtterHeltUttak = findAntallArInntektEtterHeltUttak(inntekter, forsteUttak.datoFom),
                tpForholdListe = tpOrdningStillingsprosentMap.map { (key, value) -> TpForhold(key.tpId, key.tssId, value) }
        )
    }

    fun findInntektOnDate(inntektList: List<Inntekt>, date: LocalDate) =
            inntektList.firstOrNull { inntekt: Inntekt -> inntekt.datoFom == date }

    fun findInntektSumOnDate(inntektList: List<Inntekt>, date: LocalDate) =
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