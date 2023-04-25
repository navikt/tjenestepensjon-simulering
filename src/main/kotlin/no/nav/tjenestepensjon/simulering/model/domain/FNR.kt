package no.nav.tjenestepensjon.simulering.model.domain

import com.fasterxml.jackson.annotation.JsonValue
import java.time.LocalDate
import javax.xml.bind.annotation.XmlAccessType.FIELD
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlTransient
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(FIELD)
@XmlType(name = "", propOrder = ["fnr"])

data class FNR(
        @get:JsonValue val fnr: String
) {
    @XmlTransient
    val birthDate: LocalDate = LocalDate.of(fnr.fourDigitBirthYear, fnr.adjustedMonth, fnr.adjustedDay)

    private val String.day
        get() = substring(0, 2).toInt()

    private val String.month
        get() = substring(2, 4).toInt()

    private val String.year
        get() = substring(4, 6).toInt()


    /**
     * NOTE: The original version of this method is implemented as part of the Stelvio framework. This is a refitted version.
     * See Stelvio Pid.java#get4DigitYearOfBirthWithAdjustedFnr
     * Returns a 4-digit birth year derived from a two digit year.
     *
     * @return 4 digit birth year, -1 if invalid
     */
    private val String.fourDigitBirthYear
        get() = fnr.substring(6, 9).toInt().let { individnr ->
            when {
                individnr < 500 -> 1900 + year
                individnr < 750 && year < 54 -> 1800 + year
                individnr < 1000 && year < 40 -> 2000 + year
                individnr in 900..999 && year > 39 -> 1900 + year
                else -> -1
            }
        }

    fun datoAtAge(alder: Long, maned: Long, manedIsSluttManed: Boolean): LocalDate =
            birthDate
                    .plusMonths(maned)
                    .plusYears(alder)
                    .let { it.withDayOfMonth(if (manedIsSluttManed) it.lengthOfMonth() else 1) }


    /**
     * Checks that a day may be a D-nummer. In a D-nummer 40 is added to the day part of the date
     */
    private val Int.isDnrDay
        get() = this in 41..71

    private val Int.adjustDnrDay
        get() = minus(40)

    private val String.adjustedDay
        get() = if (day.isDnrDay) {
            day.adjustDnrDay
        } else {
            day
        }

    /**
     * Removes adjustments from the month.
     * - Bost adds 20
     * - Dolly adds 40
     * - Tenor adds 80
     */
    private val String.adjustedMonth
        get() = when (month) {
            // BOST-number
            in 21..32 -> month - 20
            // Dolly test population
            in 41..52 -> month - 40
            // Tenor test population
            in 81..92 -> month - 80
            else -> month
        }

    override fun toString() = fnr
}