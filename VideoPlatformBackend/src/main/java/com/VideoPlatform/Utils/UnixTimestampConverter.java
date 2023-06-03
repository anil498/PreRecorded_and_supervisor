package com.VideoPlatform.Utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Date;

@Converter
public class UnixTimestampConverter implements AttributeConverter<Date, Long> {

    @Override
    public Long convertToDatabaseColumn(Date date) {
        if (date != null) {
            return date.getTime();
        }
        return null;
    }

    @Override
    public Date convertToEntityAttribute(Long unixTimestamp) {
        if (unixTimestamp != null) {
            return new Date(unixTimestamp);
        }
        return null;
    }
}
