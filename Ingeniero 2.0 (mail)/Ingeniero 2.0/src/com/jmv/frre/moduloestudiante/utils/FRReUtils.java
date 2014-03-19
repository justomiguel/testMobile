package com.jmv.frre.moduloestudiante.utils;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import static java.util.Collections.unmodifiableList;
import static javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED;
import static javax.xml.datatype.DatatypeFactory.newInstance;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class FRReUtils {
    public static <T> boolean isNull(T value) {
        return value == null;
    }



    public static <T> boolean isNotNull(T value) {
        return value != null;
    }

    /**
     * Added to help isolate NPEs where the getters are chained deeply on a single line.
     *
     *
     * @param value The value which is expected to be non-null
     * @param fieldName the name of the value to be checked to make Exception details useful
     * @return The value of type V (inferred) if it is not null.
     * @throws RuntimeException if the value is null
     */
    public static <V> V verifyNotNull(V value, String fieldName) {
        if (isNotNull(value)) {
            return value;
        }
        throw new RuntimeException(String.format("Value must not be null for field %s", fieldName));
    }

    public static String convertToNullIfEmpty(String str){
        return (StringUtils.isNotBlank(str) ? str : null);
    }

    public static int parseIntSafe(String stringToParseAsInt) {
        int intValue = 0;
        if (StringUtils.isNotBlank(stringToParseAsInt)) {
            try {
                intValue = Integer.parseInt(stringToParseAsInt.trim());
            } catch (NumberFormatException e) {
            }
        }
        return intValue;
    }

    public static long parseLongSafe(String stringToParseAsLong) {
        long longValue = 0;
        if (StringUtils.isNotBlank(stringToParseAsLong)) {
            try {
                longValue = Long.parseLong(stringToParseAsLong);
            } catch (NumberFormatException e) {
            }
        }
        return longValue;
    }

    public static <T> T nonNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static <T> List<T> unmodifiableNonNullList(List<T> list) {
        return list == null ? Collections.<T>emptyList() : unmodifiableList(list);
    }

    public static String nonNull(String str) {
        return String.valueOf(str);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static String isEmpty(String str, String defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String strip(final String str, final int maxLength) {
        String nonNullString = nonNull(str, EMPTY);
        return nonNullString.length() < maxLength ? nonNullString : nonNullString.substring(0, maxLength);
    }

    // Not a good name to be static imported...
    public static String toString(Object o) {
        return String.valueOf(o);
    }

    public static String toString(Object o, String nullDefault) {
        return o != null ? String.valueOf(o) : toString(nullDefault);
    }

    public static int hashCode(Object o) {
        return o != null ? o.hashCode() : 0;
    }

    public static boolean isEqualIgnoreCase(final String s1, final String s2) {
        return s1 != null ? s1.equalsIgnoreCase(s2) : s2 == null;
    }

    public static boolean isEqualIgnoreCase(final String s1, final String s2, final String desiredS2ForNull) {
        return s1 != null ? s1.equalsIgnoreCase(s2) : s2.equals(desiredS2ForNull);
    }


    public static XMLGregorianCalendar createEmptyXmlGregorianCalendar() {
        try {
            return newInstance().newXMLGregorianCalendarDate(FIELD_UNDEFINED, FIELD_UNDEFINED, FIELD_UNDEFINED, FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException e) {
            return null;
        }
    }

    public static String removeLeadingZeroes(String num) {
        String regex = "^0*";
        return num.replaceAll(regex, "");
    }

    public static String removeLeadingZeroesSafe(String num) {
        long removedZeroes = parseLongSafe(num);
        return removedZeroes == 0 ? null : String.valueOf(removedZeroes);
    }

    public static <T> T first(Collection<T> collection) {
        Iterator<T> iterator = collection.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    public static boolean isNullOrEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static String getFirstCharacter(String input) {
        String firstChar = null;
        if (isNotEmpty(input)) {
            firstChar = toString(input.charAt(0));
        }
        return firstChar;
    }

    public static boolean isStringValidLength(String input, int length) {
        boolean isStringValidLength = false;
        if (isNotEmpty(input)) {
            isStringValidLength = input.length() == length;
        }
        return isStringValidLength;
    }

    public static String capitalizeName(String name) {
        if (isNotEmpty(name)) {
            return StringUtils.capitalize(name.toLowerCase());
        }
        return "";
    }


}
