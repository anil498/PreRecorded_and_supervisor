package io.openvidu.call.java.Constants;

import java.util.HashMap;
import java.util.Map;

public enum WebhookEvent {
  SESSIONCREATED("SESSIONCREATED","SESSIONCREATED"),
  SESSIONDESTROYED("SESSIONDESTROYED","SESSIONDESTOROYED"),
  PARTICIPANTJOINED("PARTICIPANTJOINED","PARTICIPANTJOINED"),
  PARTICIPANTLEFT("PARTICIPANTLEFT","PARTICIPANTLEFT");
  private String key;
  private String value;
  private static Map<String, WebhookEvent> key2EnumMap;

  WebhookEvent(String key, String value) {
    this.key = key;
    this.value = value;
    getKey2EnumMap().put(key, this);
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public static Map<String, WebhookEvent> getKey2EnumMap() {
    if (key2EnumMap == null) {
      key2EnumMap = new HashMap<>();
    }
    return key2EnumMap;
  }
  public static WebhookEvent getEnumByKey(String key){
    return key2EnumMap.get(key);
  }

  public static void setKey2EnumMap(Map<String, WebhookEvent> key2EnumMap) {
    WebhookEvent.key2EnumMap = key2EnumMap;
  }
}
