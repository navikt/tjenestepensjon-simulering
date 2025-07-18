package no.nav.tjenestepensjon.simulering.v2.models

import no.nav.tjenestepensjon.simulering.model.domain.pen.*
import no.nav.tjenestepensjon.simulering.v2.models.DtoToV2DomainMapper.convertToLocalDate
import no.nav.tjenestepensjon.simulering.v2.models.DtoToV2DomainMapper.toSimulerPensjonRequestV2
import no.nav.tjenestepensjon.simulering.v2.models.domain.SivilstandCodeEnum
import org.junit.jupiter.api.Test
import java.time.LocalDate

import java.util.*
import kotlin.test.assertEquals

class DtoToV2DomainMapperTest {

    @Test
    fun toSimulerPensjonRequestV2() {
        val req = DummyRequest.create()

        val res = req.toSimulerPensjonRequestV2()
        assertEquals(req.fnr, res.fnr.fnr)
        // Correctly parse the expected date string into a LocalDate for comparison
        assertEquals(req.fodselsdato, res.fodselsdato)
        assertEquals(req.sisteTpnr, res.sisteTpnr)
        assertEquals(req.sprak, res.sprak)
        assertEquals(req.simulertAFPOffentlig?.simulertAFPOffentligBrutto, res.simulertAFPOffentlig!!.simulertAFPOffentligBrutto)
        assertEquals(req.simulertAFPOffentlig?.tpi, res.simulertAFPOffentlig!!.tpi)
        assertEquals(req.simulertAFPPrivat?.afpOpptjeningTotalbelop, res.simulertAFPPrivat!!.afpOpptjeningTotalbelop)
        assertEquals(req.simulertAFPPrivat?.kompensasjonstillegg, res.simulertAFPPrivat!!.kompensasjonstillegg)
        assertEquals(req.sivilstandkode, res.sivilstandkode)

        // Using assertEquals for all comparisons, including LocalDate
        assertEquals(req.inntektListe[0].datoFom.convertToLocalDate(), res.inntektListe[0].datoFom)
        assertEquals(req.inntektListe[0].inntekt, res.inntektListe[0].inntekt)

        assertEquals(req.pensjonsbeholdningsperiodeListe[0].datoFom.convertToLocalDate(), res.pensjonsbeholdningsperiodeListe[0].datoFom)
        assertEquals(req.pensjonsbeholdningsperiodeListe[0].pensjonsbeholdning.toInt(), res.pensjonsbeholdningsperiodeListe[0].pensjonsbeholdning)
        assertEquals(req.pensjonsbeholdningsperiodeListe[0].garantipensjonsbeholdning.toInt(), res.pensjonsbeholdningsperiodeListe[0].garantipensjonsbeholdning)
        assertEquals(req.pensjonsbeholdningsperiodeListe[0].garantitilleggsbeholdning.toInt(), res.pensjonsbeholdningsperiodeListe[0].garantitilleggsbeholdning)

        assertEquals(req.simuleringsperiodeListe[0].datoFom.convertToLocalDate(), res.simuleringsperiodeListe[0].datoFom)
        assertEquals(req.simuleringsperiodeListe[0].folketrygdUttaksgrad, res.simuleringsperiodeListe[0].folketrygdUttaksgrad)
        assertEquals(req.simuleringsperiodeListe[0].stillingsprosentOffentlig, res.simuleringsperiodeListe[0].stillingsprosentOffentlig)
        assertEquals(req.simuleringsperiodeListe[0].simulerAFPOffentligEtterfulgtAvAlder, res.simuleringsperiodeListe[0].simulerAFPOffentligEtterfulgtAvAlder)

        assertEquals(req.simuleringsdataListe[0].datoFom.convertToLocalDate(), res.simuleringsdataListe[0].datoFom)
        assertEquals(req.simuleringsdataListe[0].andvendtTrygdetid, res.simuleringsdataListe[0].andvendtTrygdetid)
        assertEquals(req.simuleringsdataListe[0].poengArTom1991, res.simuleringsdataListe[0].poengArTom1991)
        assertEquals(req.simuleringsdataListe[0].poengArFom1992, res.simuleringsdataListe[0].poengArFom1992)
        assertEquals(req.simuleringsdataListe[0].uforegradVedOmregning, res.simuleringsdataListe[0].uforegradVedOmregning)

        val reqForholdsListe = req.tpForholdListe!!
        assertEquals(reqForholdsListe[0].tpnr, res.tpForholdListe[0].tpnr)
        assertEquals(reqForholdsListe[0].opptjeningsperiodeListe[0].stillingsprosent, res.tpForholdListe[0].opptjeningsperiodeListe[0].stillingsprosent.toInt())
        assertEquals(reqForholdsListe[0].opptjeningsperiodeListe[0].datoFom.convertToLocalDate(), res.tpForholdListe[0].opptjeningsperiodeListe[0].datoFom)
        assertEquals(reqForholdsListe[0].opptjeningsperiodeListe[0].datoTom.convertToLocalDate(), res.tpForholdListe[0].opptjeningsperiodeListe[0].datoTom)
        assertEquals(reqForholdsListe[0].opptjeningsperiodeListe[0].faktiskHovedlonn, res.tpForholdListe[0].opptjeningsperiodeListe[0].faktiskHovedlonn)
        assertEquals(reqForholdsListe[0].opptjeningsperiodeListe[0].stillingsuavhengigTilleggslonn, res.tpForholdListe[0].opptjeningsperiodeListe[0].stillingsuavhengigTilleggslonn)
        assertEquals(reqForholdsListe[0].opptjeningsperiodeListe[0].aldersgrense, res.tpForholdListe[0].opptjeningsperiodeListe[0].aldersgrense)


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
