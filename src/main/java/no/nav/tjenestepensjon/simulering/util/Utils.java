package no.nav.tjenestepensjon.simulering.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class Utils {

    /**
     * NOTE: The original version of this method is implemented as part of the Stelvio framework. This is a refitted version.
     * See Stelvio Pid.java#get4DigitYearOfBirthWithAdjustedFnr
     * Returns a 4-digit birth date derived from a two digit year date.
     *
     * @param fnr fnr
     * @return 4 digit birth date, -1 if invalid
     */
    public static int get4DigitBirthYear(String fnr) {
        int year = Integer.parseInt(fnr.substring(4, 6));
        int individnr = Integer.parseInt(fnr.substring(6, 9));

        if (individnr < 500) {
            year += 1900;
        } else if ((individnr < 750) && (54 < year)) {
            year += 1800;
        } else if ((individnr < 1000) && (year < 40)) {
            year += 2000;
        } else if ((900 <= individnr) && (individnr < 1000) && (39 < year)) {
            year += 1900;
        } else {
            // invalid fnr
            return -1;
        }
        return year;
    }

    public static Date getBirthDate(String fnr) {
        int day = Integer.parseInt(fnr.substring(0, 2));
        int month = Integer.parseInt(fnr.substring(2, 4));
        Calendar birthdate = Calendar.getInstance();
        birthdate.set(get4DigitBirthYear(fnr), month - 1, day);
        return birthdate.getTime();
    }

    public static Date createDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTime();
    }

    public static Calendar createDayResolutionCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static boolean isSameDay(Date first, Date second) {
        Calendar firstCalendar = createDayResolutionCalendar(first);
        Calendar secondCalendar = createDayResolutionCalendar(second);
        return firstCalendar.equals(secondCalendar);
    }

    public static XMLGregorianCalendar convertToXmlGregorianCalendar(Date date) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Exception while getting forsteUttakDato", e);
        }
    }

    public static Date convertToDato(String fnr, Integer startAlder, Integer startManed) {
        var startDato = createDayResolutionCalendar(getBirthDate(fnr));
        startDato.set(Calendar.DATE, 1);
        startDato.add(Calendar.MONTH, startManed);
        startDato.add(Calendar.YEAR, startAlder);
        return startDato.getTime();
    }
}
