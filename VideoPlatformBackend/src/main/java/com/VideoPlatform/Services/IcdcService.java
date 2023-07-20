package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.IcdcEntity;
import com.VideoPlatform.Entity.IcdcResponseEntity;

public interface IcdcService {
    IcdcResponseEntity saveIcdcResponse(IcdcResponseEntity icdcResponseEntity);
    IcdcEntity createIcdc(IcdcEntity icdcEntity, String authKey, String token);

}
