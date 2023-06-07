package com.VideoPlatform.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.VideoPlatform.Constant.AllConstants.DATE_FORMATTER;

public class TimeUtils {
    public static SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMATTER);

    public static Date getDate() {

        Calendar calendar = Calendar.getInstance();
        Date currentDateTime = calendar.getTime();
        return currentDateTime;
    }
    public static Boolean isExpire(Date expTime){
        try {

            Date currentDateTime = new Date();
            if(currentDateTime.before(expTime)){
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    public static Date increaseDateTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY,24);
        Date newDateTime = calendar.getTime();
        return newDateTime;
    }
    public static Date parseDate(String date){
        Date newDate = null;
        try {
            newDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }
}
