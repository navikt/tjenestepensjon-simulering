package no.nav.tjenestepensjon.simulering.util

import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import java.time.LocalDate
import kotlin.reflect.KFunction

fun convertToDato(fnr: FNR, alder: Long, maned: Long, manedIsSluttManed: Boolean): LocalDate =
        fnr.birthDate
                .plusMonths(maned)
                .plusYears(alder)
                .let { it.withDayOfMonth(if (manedIsSluttManed) it.lengthOfMonth() else 1) }


fun getHeaderFromRequestContext(key: String): String {
    return RequestContextHolder.currentRequestAttributes().getAttribute(key, RequestAttributes.SCOPE_REQUEST).toString()
}