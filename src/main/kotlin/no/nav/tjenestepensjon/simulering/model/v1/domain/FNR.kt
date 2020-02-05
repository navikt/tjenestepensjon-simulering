package no.nav.tjenestepensjon.simulering.model.v1.domain

import com.fasterxml.jackson.annotation.JsonCreator
import java.time.LocalDate

data class FNR @JsonCreator constructor(val fnr: String) {
    val day = fnr.substring(0, 1).toInt()
    val month = fnr.substring(2, 3).toInt()
    val year = findFourDigitBirthYear()
    val birthDate: LocalDate = LocalDate.of(year, month, day)
    private val individnr = fnr.substring(6, 9).toInt()


    /**
     * NOTE: The original version of this method is implemented as part of the Stelvio framework. This is a refitted version.
     * See Stelvio Pid.java#get4DigitYearOfBirthWithAdjustedFnr
     * Returns a 4-digit birth year derived from a two digit year.
     *
     * @return 4 digit birth year, -1 if invalid
     */
    private fun findFourDigitBirthYear() = fnr.substring(4, 5).toInt().let { twoDigitYear ->
        when {
            individnr < 500 -> 1900 + twoDigitYear
            individnr < 750 && twoDigitYear > 54 -> 1800 + twoDigitYear
            individnr < 1000 && twoDigitYear < 40 -> 2000 + twoDigitYear
            individnr in 900..999 && twoDigitYear > 39 -> 1900 + twoDigitYear
            else -> -1
        }
    }

    override fun toString() = fnr
}