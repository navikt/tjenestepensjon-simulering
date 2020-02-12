package no.nav.tjenestepensjon.simulering.model.v1.domain

import com.fasterxml.jackson.annotation.JsonValue
import java.time.LocalDate

data class FNR(
        @get:JsonValue val fnr: String
) {
    private val individnr = fnr.substring(6, 9).toInt()
    private val day = fnr.substring(0, 2).toInt()
    private val month = fnr.substring(2, 4).toInt()
    private val year = findFourDigitBirthYear()
    val birthDate: LocalDate = LocalDate.of(year, month, day)


    /**
     * NOTE: The original version of this method is implemented as part of the Stelvio framework. This is a refitted version.
     * See Stelvio Pid.java#get4DigitYearOfBirthWithAdjustedFnr
     * Returns a 4-digit birth year derived from a two digit year.
     *
     * @return 4 digit birth year, -1 if invalid
     */
    private fun findFourDigitBirthYear() = fnr.substring(4, 6).toInt().let { twoDigitYear ->
        when {
            individnr < 500 -> 1900 + twoDigitYear
            individnr < 750 && twoDigitYear < 54 -> 1800 + twoDigitYear
            individnr < 1000 && twoDigitYear < 40 -> 2000 + twoDigitYear
            individnr in 900..999 && twoDigitYear > 39 -> 1900 + twoDigitYear
            else -> -1
        }
    }

    fun datoAtAge(alder: Long, maned: Long, manedIsSluttManed: Boolean): LocalDate =
            birthDate
                    .plusMonths(maned)
                    .plusYears(alder)
                    .let { it.withDayOfMonth(if (manedIsSluttManed) it.lengthOfMonth() else 1) }

    override fun toString() = fnr
}