package no.nav.tjenestepensjon.simulering.v2.models

import no.nav.tjenestepensjon.simulering.model.domain.pen.*
import no.nav.tjenestepensjon.simulering.v2.models.DtoToV2DomainMapper.convertToLocalDate
import no.nav.tjenestepensjon.simulering.v2.models.DtoToV2DomainMapper.toSimulerPensjonRequestV2
import no.nav.tjenestepensjon.simulering.v2.models.domain.SivilstandCodeEnum
import org.junit.jupiter.api.Test
import java.time.LocalDate

import java.util.*

class DtoToV2DomainMapperTest {

    @Test
    fun toSimulerPensjonRequestV2() {
        val req = DummyRequest.create()

        val res = req.toSimulerPensjonRequestV2()
        assert(res.fnr.fnr == req.fnr)
        assert(res.fodselsdato == req.fodselsdato)
        assert(res.sisteTpnr == req.sisteTpnr)
        assert(res.sprak == req.sprak)
        assert(res.simulertAFPOffentlig!!.simulertAFPOffentligBrutto == req.simulertAFPOffentlig?.simulertAFPOffentligBrutto)
        assert(res.simulertAFPOffentlig!!.tpi == req.simulertAFPOffentlig?.tpi)
        assert(res.simulertAFPPrivat!!.afpOpptjeningTotalbelop == req.simulertAFPPrivat?.afpOpptjeningTotalbelop)
        assert(res.simulertAFPPrivat!!.kompensasjonstillegg == req.simulertAFPPrivat?.kompensasjonstillegg)
        assert(res.sivilstandkode == req.sivilstandkode)
        assert(res.inntektListe[0].datoFom == req.inntektListe[0].datoFom.convertToLocalDate())
        assert(res.inntektListe[0].inntekt == req.inntektListe[0].inntekt)
        assert(res.pensjonsbeholdningsperiodeListe[0].datoFom == req.pensjonsbeholdningsperiodeListe[0].datoFom.convertToLocalDate())
        assert(res.pensjonsbeholdningsperiodeListe[0].pensjonsbeholdning == req.pensjonsbeholdningsperiodeListe[0].pensjonsbeholdning.toInt())
        assert(res.pensjonsbeholdningsperiodeListe[0].garantipensjonsbeholdning == req.pensjonsbeholdningsperiodeListe[0].garantipensjonsbeholdning.toInt())
        assert(res.pensjonsbeholdningsperiodeListe[0].garantitilleggsbeholdning == req.pensjonsbeholdningsperiodeListe[0].garantitilleggsbeholdning.toInt())
        assert(res.simuleringsperiodeListe[0].datoFom == req.simuleringsperiodeListe[0].datoFom.convertToLocalDate())
        assert(res.simuleringsperiodeListe[0].folketrygdUttaksgrad == req.simuleringsperiodeListe[0].folketrygdUttaksgrad)
        assert(res.simuleringsperiodeListe[0].stillingsprosentOffentlig == req.simuleringsperiodeListe[0].stillingsprosentOffentlig)
        assert(res.simuleringsperiodeListe[0].simulerAFPOffentligEtterfulgtAvAlder == req.simuleringsperiodeListe[0].simulerAFPOffentligEtterfulgtAvAlder)
        assert(res.simuleringsdataListe[0].datoFom == req.simuleringsdataListe[0].datoFom.convertToLocalDate())
        assert(res.simuleringsdataListe[0].andvendtTrygdetid == req.simuleringsdataListe[0].andvendtTrygdetid)
        assert(res.simuleringsdataListe[0].poengArTom1991 == req.simuleringsdataListe[0].poengArTom1991)
        assert(res.simuleringsdataListe[0].poengArFom1992 == req.simuleringsdataListe[0].poengArFom1992)
        assert(res.simuleringsdataListe[0].uforegradVedOmregning == req.simuleringsdataListe[0].uforegradVedOmregning)
        val reqForholdsListe = req.tpForholdListe!!
        assert(res.tpForholdListe[0].tpnr == reqForholdsListe[0].tpnr)
        assert(res.tpForholdListe[0].opptjeningsperiodeListe[0].stillingsprosent.toInt() == reqForholdsListe[0].opptjeningsperiodeListe[0].stillingsprosent)
        assert(res.tpForholdListe[0].opptjeningsperiodeListe[0].datoFom == reqForholdsListe[0].opptjeningsperiodeListe[0].datoFom.convertToLocalDate())
        assert(res.tpForholdListe[0].opptjeningsperiodeListe[0].datoTom == reqForholdsListe[0].opptjeningsperiodeListe[0].datoTom.convertToLocalDate())
        assert(res.tpForholdListe[0].opptjeningsperiodeListe[0].faktiskHovedlonn == reqForholdsListe[0].opptjeningsperiodeListe[0].faktiskHovedlonn)
        assert(res.tpForholdListe[0].opptjeningsperiodeListe[0].stillingsuavhengigTilleggslonn == reqForholdsListe[0].opptjeningsperiodeListe[0].stillingsuavhengigTilleggslonn)
        assert(res.tpForholdListe[0].opptjeningsperiodeListe[0].aldersgrense == reqForholdsListe[0].opptjeningsperiodeListe[0].aldersgrense)

    }

    object DummyRequest {

        fun create() = SimulerOffentligTjenestepensjonRequest(
            fnr = "01505801195",
            fodselsdato = LocalDate.of(1958, 10, 1).toString(),
            sisteTpnr = "sisteTpnr",
            sprak = "sprak",
            simulertAFPOffentlig = SimulertAFPOffentligDto(
                simulertAFPOffentligBrutto = 1,
                tpi = 2
            ),
            simulertAFPPrivat = SimulertAFPPrivatDto(
                afpOpptjeningTotalbelop = 3,
                kompensasjonstillegg = 4.0
            ),
            sivilstandkode = SivilstandCodeEnum.GIFT,
            inntektListe = listOf(
                InntektDto(
                    datoFom = Date(),
                    inntekt = 5.0
                )
            ),
            pensjonsbeholdningsperiodeListe = listOf(
                PensjonsbeholdningsperiodeDto(
                    datoFom = Date(),
                    pensjonsbeholdning = 6.0,
                    garantipensjonsbeholdning = 7.0,
                    garantitilleggsbeholdning = 8.0
                )
            ),
            simuleringsperiodeListe = listOf(
                SimuleringsperiodeDto(
                    datoFom = Date(),
                    folketrygdUttaksgrad = 9,
                    stillingsprosentOffentlig = 10,
                    simulerAFPOffentligEtterfulgtAvAlder = true
                )
            ),
            simuleringsdataListe = listOf(
                SimuleringsdataDto(
                    datoFom = Date(),
                    andvendtTrygdetid = 11,
                    poengArTom1991 = 12,
                    poengArFom1992 = 13,
                    uforegradVedOmregning = 14,
                    basisgp = 15.0,
                    basispt = 16.0,
                    basistp = 17.0,
                    delingstallUttak = 18.0,
                    forholdstallUttak = 19.0,
                    sluttpoengtall = 20.0
                )
            ),
            tpForholdListe = listOf(
                TpForholdDto(
                    tpnr = "tpnr",
                    opptjeningsperiodeListe = listOf(
                        OpptjeningsperiodeDto(
                            stillingsprosent = 15,
                            datoFom = Date(),
                            datoTom = Date(),
                            faktiskHovedlonn = 16,
                            stillingsuavhengigTilleggslonn = 17,
                            aldersgrense = 18
                        )
                    )
                )
            )
        )
    }
}
