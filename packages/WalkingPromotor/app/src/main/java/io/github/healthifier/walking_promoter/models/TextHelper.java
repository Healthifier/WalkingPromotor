package io.github.healthifier.walking_promoter.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TextHelper {

    private static final long UNIT_MAN = 10000;
    private static final long UNIT_OKU = UNIT_MAN * UNIT_MAN;

    private TextHelper() {
    }

    public static String iso8601(Date date) {
//        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
//        df.setTimeZone(tz);
        return df.format(date);
    }

    public static String floorJpUnit(long value) {
        if (value >= UNIT_OKU) {
            if (value < 10 * UNIT_OKU) {
                return String.format("%.1f億", 1.0 * value / UNIT_OKU);
            }
            return (value / UNIT_OKU) + "億";
        }
        if (value >= UNIT_MAN) {
            if (value < 10 * UNIT_MAN) {
                return String.format("%.1f万", 1.0 * value / UNIT_MAN);
            }
            return (value / UNIT_MAN) + "万";
        }
        return String.valueOf(value);
    }
}
