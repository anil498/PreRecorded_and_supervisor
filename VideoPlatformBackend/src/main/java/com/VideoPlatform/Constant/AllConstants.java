package com.VideoPlatform.Constant;

import java.time.format.DateTimeFormatter;

public class AllConstants {
    public static final Object SUBMISSION_STATE ="SUBMIT_FAILED";
    public static final String DATE_FORMATTER= "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
}
