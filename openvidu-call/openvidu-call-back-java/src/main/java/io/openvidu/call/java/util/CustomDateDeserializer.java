package io.openvidu.call.java.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public  class CustomDateDeserializer extends JsonDeserializer<Date> {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  @Override
  public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, IOException {
    String dateString = jsonParser.getText();
    try {
      return DATE_FORMAT.parse(dateString);
    } catch (ParseException e) {
      throw new IllegalArgumentException("Invalid date format: " + dateString, e);
    }
  }
}

