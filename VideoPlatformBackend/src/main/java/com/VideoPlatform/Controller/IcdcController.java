package com.VideoPlatform.Controller;

import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Entity.IcdcEntity;
import com.VideoPlatform.Entity.IcdcResponseEntity;
import com.VideoPlatform.Entity.UserEntity;
import com.VideoPlatform.Repository.IcdcRepository;
import com.VideoPlatform.Repository.IcdcResponseRepository;
import com.VideoPlatform.Services.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICALLICDC)
public class IcdcController {

    @Autowired
    private CommonService commonService;
    @Autowired
    private IcdcRepository icdcRepository;
    @Autowired
    private IcdcResponseRepository icdcResponseRepository;

    private static final Logger logger= LoggerFactory.getLogger(UserController.class);
//    @PostMapping("/Create")
//    public ResponseEntity<?> createIcdc(@RequestBody IcdcEntity icdcEntity, HttpServletRequest request) {
//
////        String authKey = request.getHeader("Authorization");
////        String token = request.getHeader("Token");
////        if(!commonService.authorizationCheck(authKey,token,"icdc_creation")){
////            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
////        }
//
//    }

    @PostMapping("/Save")
    public ResponseEntity<?> icdcResponse(@RequestBody IcdcResponseEntity icdcResponseEntity, HttpServletRequest request) {
        icdcResponseRepository.save(icdcResponseEntity);
        return new ResponseEntity<>(commonService.responseData("200","Response saved!"),HttpStatus.OK);
    }
}