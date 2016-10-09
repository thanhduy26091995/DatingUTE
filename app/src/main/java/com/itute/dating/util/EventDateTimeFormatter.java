package com.itute.dating.util;

import java.text.SimpleDateFormat;

/**
 * Created by Mai Thanh Hiep on 7/10/2016.
 */
public class EventDateTimeFormatter {
    public static String formatDateStart(long milliseconds) {
        return new SimpleDateFormat("dd - MM - yyyy").format(milliseconds);
    }

    public static String formatEventTime(long milliseconds) {
        return new SimpleDateFormat("h:mm a").format(milliseconds);
    }
}
