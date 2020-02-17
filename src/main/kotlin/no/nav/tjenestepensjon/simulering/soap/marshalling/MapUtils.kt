package no.nav.tjenestepensjon.simulering.soap.marshalling

import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar


fun XMLGregorianCalendar.toLocalDate() =
        toGregorianCalendar().toZonedDateTime().toLocalDate()

fun LocalDate.toXMLGregorianCalendar(): XMLGregorianCalendar =
        DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(atStartOfDay(ZoneId.systemDefault())))