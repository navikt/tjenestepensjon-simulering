package no.nav.tjenestepensjon.simulering.util

import no.nav.tjenestepensjon.simulering.AsyncExecutor.AsyncResponse
import no.nav.tjenestepensjon.simulering.StillingsprosentCallable
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import java.time.LocalDate

typealias TPOrdningStillingsprosentMap = Map<TPOrdning, List<Stillingsprosent>>
typealias TPOrdningStillingsprosentCallableMap = Map<TPOrdning, StillingsprosentCallable>
typealias TPOrdningTpLeverandorMap = Map<TPOrdning, TpLeverandor>
open class TPOrdningStillingsprosentResponseClass : AsyncResponse<TPOrdning, List<Stillingsprosent>>()

fun convertToDato(fnr: FNR, alder: Long, maned: Long, manedIsSluttManed: Boolean): LocalDate =
        fnr.birthDate
                .plusMonths(maned)
                .plusYears(alder)
                .let { it.withDayOfMonth(if (manedIsSluttManed) it.lengthOfMonth() else 1) }


fun getHeaderFromRequestContext(key: String): String {
    return RequestContextHolder.currentRequestAttributes().getAttribute(key, RequestAttributes.SCOPE_REQUEST)!!.toString()
}