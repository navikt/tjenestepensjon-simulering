package no.nav.tjenestepensjon.simulering.mapper

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.tjenestepensjon.simulering.mapper.StillingsprosentMapper.mapToStillingsprosent
import no.nav.tjenestepensjon.simulering.util.Utils.convertToXmlGregorianCalendar
import no.nav.tjenestepensjon.simulering.util.Utils.createDate
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Month
import java.util.*

internal class StillingsprosentMapperTest {
    @Test
    fun stillingsprosent_v1_maps_to_domain() {
        val original = Stillingsprosent()
        original.setStillingsprosent(100.0)
        original.setDatoFom(convertToXmlGregorianCalendar(createDate(2015, Calendar.FEBRUARY, 14)))
        original.setDatoTom(convertToXmlGregorianCalendar(createDate(2015, Calendar.MARCH, 14)))
        original.setFaktiskHovedlonn("0")
        original.setStillingsuavhengigTilleggslonn("100")
        original.setAldersgrense(0)
        val expected: `var` = Stillingsprosent()
        expected.setStillingsprosent(100.0)
        expected.setDatoFom(LocalDate.of(2015, Month.FEBRUARY, 14))
        expected.setDatoTom(LocalDate.of(2015, Month.MARCH, 14))
        expected.setFaktiskHovedlonn("0")
        expected.setStillingsuavhengigTilleggslonn("100")
        expected.setAldersgrense(0)
        val actual: `var` = mapToStillingsprosent(original)
        assertEquals(expected.getStillingsprosent(), actual.getStillingsprosent())
        assertEquals(expected.getDatoFom(), actual.getDatoFom())
        assertEquals(expected.getDatoTom(), actual.getDatoTom())
        assertEquals(expected.getFaktiskHovedlonn(), actual.getFaktiskHovedlonn())
        assertEquals(expected.getStillingsuavhengigTilleggslonn(), actual.getStillingsuavhengigTilleggslonn())
        assertEquals(expected.getAldersgrense(), actual.getAldersgrense())
    }

    @Test
    fun domainMapsToStillingsprosentv1() {
        val original = Stillingsprosent()
        original.setStillingsprosent(100.0)
        original.setDatoFom(LocalDate.of(2015, Month.FEBRUARY, 14))
        original.setDatoTom(LocalDate.of(2015, Month.MARCH, 14))
        original.setFaktiskHovedlonn("0")
        original.setStillingsuavhengigTilleggslonn("100")
        original.setAldersgrense(0)
        val expected: `var` = Stillingsprosent()
        expected.setStillingsprosent(100.0)
        expected.setDatoFom(convertToXmlGregorianCalendar(createDate(2015, Calendar.FEBRUARY, 14)))
        expected.setDatoTom(convertToXmlGregorianCalendar(createDate(2015, Calendar.MARCH, 14)))
        expected.setFaktiskHovedlonn("0")
        expected.setStillingsuavhengigTilleggslonn("100")
        expected.setAldersgrense(0)
        val actual: `var` = mapToStillingsprosent(original)
        assertEquals(expected.getStillingsprosent(), actual.getStillingsprosent())
        assertEquals(expected.getDatoFom(), actual.getDatoFom())
        assertEquals(expected.getDatoTom(), actual.getDatoTom())
        assertEquals(expected.getFaktiskHovedlonn(), actual.getFaktiskHovedlonn())
        assertEquals(expected.getStillingsuavhengigTilleggslonn(), actual.getStillingsuavhengigTilleggslonn())
        assertEquals(expected.getAldersgrense(), actual.getAldersgrense())
    }
}