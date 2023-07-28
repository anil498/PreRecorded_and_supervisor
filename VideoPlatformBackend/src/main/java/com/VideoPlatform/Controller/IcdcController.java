package com.VideoPlatform.Controller;

import com.VideoPlatform.Constant.RequestMappings;
import com.VideoPlatform.Entity.IcdcEntity;
import com.VideoPlatform.Entity.IcdcResponseEntity;
import com.VideoPlatform.Entity.UserEntity;
import com.VideoPlatform.Repository.IcdcRepository;
import com.VideoPlatform.Repository.IcdcResponseRepository;
import com.VideoPlatform.Services.CommonService;
import com.VideoPlatform.Services.IcdcService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICALLICDC)
public class IcdcController {

    @Autowired
    private CommonService commonService;
    @Autowired
    private IcdcRepository icdcRepository;
    @Autowired
    private IcdcService icdcService;
    @Autowired
    private IcdcResponseRepository icdcResponseRepository;

    private static final Logger logger= LoggerFactory.getLogger(UserController.class);
    @PostMapping("/Create")
    public ResponseEntity<?> createIcdc(@RequestBody IcdcEntity icdcEntity, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if(!commonService.authorizationCheck(authKey,token,"icdc_creation")){
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        icdcService.createIcdc(icdcEntity,authKey,token);
        return new ResponseEntity<>(commonService.responseData("200","Form Created!"),HttpStatus.OK);
    }

    @PostMapping("/Save")
    public ResponseEntity<?> icdcResponse(@RequestBody IcdcResponseEntity icdcResponseEntity, HttpServletRequest request) {
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"icdc")){
            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        logger.info("IcdcResponseEntity : {}",icdcResponseEntity);
        if(icdcService.saveIcdcResponse(icdcResponseEntity)==null)
            return new ResponseEntity<>(commonService.responseData("406","Null entity"),HttpStatus.NOT_ACCEPTABLE);
        return new ResponseEntity<>(commonService.responseData("200","Response saved!"),HttpStatus.OK);
    }

    @GetMapping("/GetAll")
    public ResponseEntity<List<IcdcEntity>> getAllIcdc(HttpServletRequest request){
        logger.info(commonService.getHeaders(request).toString());
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if(!commonService.authorizationCheck(authKey,token,"icdc")){
            return  new ResponseEntity<List<IcdcEntity>>(HttpStatus.UNAUTHORIZED);
        }
        return ok(icdcService.getAllIcdc());
    }
    @GetMapping("/GetAllByUser")
    public ResponseEntity<List<IcdcEntity>> getAllIcdcByUser(HttpServletRequest request){
        logger.info(commonService.getHeaders(request).toString());
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if(!commonService.authorizationCheck(authKey,token,"icdc")){
            return  new ResponseEntity<List<IcdcEntity>>(HttpStatus.UNAUTHORIZED);
        }
        return ok(icdcService.getAllUserIcdc(token));
    }

    @PutMapping("/Update")
    public ResponseEntity<?> updateIcdc(@RequestBody String params1, HttpServletRequest request) throws JsonProcessingException {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if(!commonService.authorizationCheck(authKey,token,"icdc_update")){
            return  new ResponseEntity<List<IcdcEntity>>(HttpStatus.UNAUTHORIZED);
        }
        return icdcService.updateIcdc(params1);
    }

    @DeleteMapping("/Delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if(!commonService.authorizationCheck(authKey,token,"icdc_delete")){
            return  new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        icdcService.deleteIcdc(id);
        return ok(commonService.responseData("200","ICDC Deleted!"));
    }

//    @PostMapping("/GetNames")
//    public ResponseEntity<?> getNames(@RequestBody Map<String,Object> params, HttpServletRequest request) {
//        String authKey = request.getHeader("Authorization");
//        String token = request.getHeader("Token");
//        if(!commonService.authorizationCheck(authKey,token,"icdc")){
//            return  new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
//        }
//        List<Map<String,Object>> list = icdcService.getNames(params);
//        return ResponseEntity.ok(list);
//    }
}
