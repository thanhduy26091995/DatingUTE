package com.itute.dating.util;

import java.util.Date;
import java.util.Locale;

/**
 * Created by Mai Thanh Hiep on 4/24/2016.
 */
public class PrettyTime {

    public static String format(Date now, Date past) {
        return TimeAgo.toDuration(now.getTime() - past.getTime());
    }
}
