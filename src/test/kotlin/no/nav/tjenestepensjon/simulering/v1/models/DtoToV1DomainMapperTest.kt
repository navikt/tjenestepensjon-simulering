package no.nav.tjenestepensjon.simulering.v1.models

import no.nav.tjenestepensjon.simulering.v1.models.DtoToV1DomainMapper.toSimulerPensjonRequestV1
import no.nav.tjenestepensjon.simulering.v2.models.DtoToV2DomainMapper.convertToLocalDate
import no.nav.tjenestepensjon.simulering.v2.models.DtoToV2DomainMapperTest
import org.junit.jupiter.api.Test

class DtoToV1DomainMapperTest {

    @Test
    fun toSimulerPensjonRequestV1() {

        val req =  DtoToV2DomainMapperTest.DummyRequest.create()
        val res = req.toSimulerPensjonRequestV1()

        assert(res.fnr.fnr == req.fnr)
        assert(res.sivilstandkode == req.sivilstandkode.toString())
        assert(res.sprak == req.sprak)
        assert(res.inntekter[0].datoFom == req.inntektListe[0].datoFom.convertToLocalDate())
        assert(res.inntekter[0].inntekt == req.inntektListe[0].inntekt)
        assert(res.simuleringsperioder[0].datoFom == req.simuleringsperiodeListe[0].datoFom.convertToLocalDate())
        assert(res.simuleringsperioder[0].utg == req.simuleringsperiodeListe[0].folketrygdUttaksgrad)
        assert(res.simuleringsperioder[0].stillingsprosentOffentlig == req.simuleringsperiodeListe[0].stillingsprosentOffentlig)
        assert(res.simulertAFPOffentlig == req.simulertAFPOffentlig?.simulertAFPOffentligBrutto)
        assert(res.simulertAFPPrivat!!.afpOpptjeningTotalbelop == req.simulertAFPPrivat?.afpOpptjeningTotalbelop)
        assert(res.simulertAFPPrivat!!.kompensasjonstillegg == req.simulertAFPPrivat?.kompensasjonstillegg)
        assert(res.pensjonsbeholdningsperioder[0].datoFom == req.pensjonsbeholdningsperiodeListe[0].datoFom.convertToLocalDate())
        assert(res.pensjonsbeholdningsperioder[0].pensjonsbeholdning == req.pensjonsbeholdningsperiodeListe[0].pensjonsbeholdning.toInt())
        assert(res.pensjonsbeholdningsperioder[0].garantipensjonsbeholdning == req.pensjonsbeholdningsperiodeListe[0].garantipensjonsbeholdning.toInt())
        assert(res.pensjonsbeholdningsperioder[0].garantitilleggsbeholdning == req.pensjonsbeholdningsperiodeListe[0].garantitilleggsbeholdning.toInt())
    }
}