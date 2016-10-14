package com.itute.dating.util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
http://stackoverflow.com/questions/3859288/how-to-calculate-time-ago-in-java
Answer by: Riccardo Casatta
 */

/**
 * Created by Mai Thanh Hiep on 4/24/2016.
 */
public class TimeAgo {
    public TimeAgo(String s) {
    }

    public static final List<Long> times = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1));
    public static final List<String> timesStringViet = Arrays.asList("năm", "tháng", "ngày", "giờ", "phút", "giây");

    public static String toDuration(long duration) {
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < TimeAgo.times.size(); i++) {
            Long current = TimeAgo.times.get(i);
            long temp = duration / current;
            if (temp > 0) {
                res.append(temp).append(" ").append(TimeAgo.timesStringViet.get(i)).append(temp > 1 ? "" : "")
                        .append(" trước");
                break;
            }
        }
        if ("".equals(res.toString()))
            return "vừa xong";

        else
            return res.toString();
    }
}