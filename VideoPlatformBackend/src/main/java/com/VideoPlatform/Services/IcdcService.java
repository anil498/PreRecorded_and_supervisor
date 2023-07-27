package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.IcdcEntity;
import com.VideoPlatform.Entity.IcdcResponseEntity;

import java.util.List;
import java.util.Map;

public interface IcdcService {
    IcdcResponseEntity saveIcdcResponse(IcdcResponseEntity icdcResponseEntity);
    IcdcEntity createIcdc(IcdcEntity icdcEntity, String authKey, String token);
//    List<Map<String,Object>> getNames(Map<String,Object> params);

}
