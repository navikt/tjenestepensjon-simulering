package no.nav.tjenestepensjon.simulering.v1.soap.marshalling

import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar


fun XMLGregorianCalendar.toLocalDate(): LocalDate =
        toGregorianCalendar().toZonedDateTime().toLocalDate()

fun LocalDate.toXMLGregorianCalendar(): XMLGregorianCalendar =
        DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(atStartOfDay(ZoneId.systemDefault())))