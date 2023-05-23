package io.openvidu.call.java.Constants;

import java.util.HashMap;
import java.util.Map;

public enum CallType {
  PRERECORDED("PRERECORDED","PRERECORDED");
  private String key;
  private String value;
  private static Map<String, CallType> key2EnumMap;

  CallType(String key, String value) {
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

  public static Map<String, CallType> getKey2EnumMap() {
    if (key2EnumMap == null) {
      key2EnumMap = new HashMap<>();
    }
    return key2EnumMap;
  }
  public static CallType getEnumByKey(String key){
    return key2EnumMap.get(key);
  }

  public static void setKey2EnumMap(Map<String, CallType> key2EnumMap) {
    CallType.key2EnumMap = key2EnumMap;
  }

}
