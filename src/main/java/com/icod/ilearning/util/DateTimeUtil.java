package com.icod.ilearning.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
    public static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    public static String toString(Date date,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

}
