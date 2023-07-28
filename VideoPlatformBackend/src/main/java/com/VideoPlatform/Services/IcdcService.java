package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.IcdcEntity;
import com.VideoPlatform.Entity.IcdcResponseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface IcdcService {
    IcdcResponseEntity saveIcdcResponse(IcdcResponseEntity icdcResponseEntity);
    IcdcEntity createIcdc(IcdcEntity icdcEntity, String authKey, String token);
//    List<Map<String,Object>> getNames(Map<String,Object> params);
    String deleteIcdc(Integer icdcId);
    ResponseEntity<?> updateIcdc(String params1) throws JsonProcessingException;
    List<IcdcEntity> getAllUserIcdc(String token);
    List<IcdcEntity> getAllIcdc();
}
