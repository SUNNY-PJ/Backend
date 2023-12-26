package com.sunny.backend.common;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DatetimeUtil {
    public static long dPlus(LocalDateTime dayBefore){
        return ChronoUnit.DAYS.between(dayBefore,LocalDateTime.now());
    }

    public static long dMinus(LocalDateTime dayAfter){
        return ChronoUnit.DAYS.between(dayAfter,LocalDateTime.now());
    }
    public static String timesAgo(LocalDateTime dayBefore) {
        ZoneId seoulZone = ZoneId.of("Asia/Seoul");
        long gap = ChronoUnit.MINUTES.between(dayBefore.atZone(seoulZone).toLocalDateTime(), ZonedDateTime.now(seoulZone).toLocalDateTime());
        String word;
        if (gap == 0){
            word = "방금 전";
        }else if (gap < 60) {
            word = gap + "분 전";
        }else if (gap < 60 * 24){
            word = (gap/60) + "시간 전";
        }else {
            word = dayBefore.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        return word;
    }

    public static String customForm(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("MM월 dd일"));
    }
}
