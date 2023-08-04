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
import java.util.List;

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

    private static final Logger logger = LoggerFactory.getLogger(IcdcController.class);

    @PostMapping("/Create")
    public ResponseEntity<?> createIcdc(@RequestBody IcdcEntity icdcEntity, HttpServletRequest request) {
        logger.info("REST API: POST {} {} Request Headers={}", RequestMappings.APICALLICDC, icdcEntity != null ? icdcEntity.toString() : "{}",commonService.getHeaders(request));
        if(icdcEntity==null)
            return new ResponseEntity<>(commonService.responseData("500","Request must contain parameter values!"),HttpStatus.INTERNAL_SERVER_ERROR);

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if (!commonService.authorizationCheck(authKey, token, "icdc_creation")) {
            return new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        icdcService.createIcdc(icdcEntity, authKey, token);
        return new ResponseEntity<>(commonService.responseData("200", "Form Created!"), HttpStatus.OK);
    }

    @PostMapping("/Save")
    public ResponseEntity<?> icdcResponse(@RequestBody IcdcResponseEntity icdcResponseEntity, HttpServletRequest request) {
        logger.info("REST API: POST {} {} Request Headers={}", RequestMappings.APICALLICDC, icdcResponseEntity != null ? icdcResponseEntity.toString() : "{}",commonService.getHeaders(request));
        if(icdcResponseEntity==null)
            return new ResponseEntity<>(commonService.responseData("500","Request must contain parameter values!"),HttpStatus.INTERNAL_SERVER_ERROR);

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if (!commonService.authorizationCheck(authKey, token, "icdc")) {
            return new ResponseEntity<UserEntity>(HttpStatus.UNAUTHORIZED);
        }
        logger.info("IcdcResponseEntity : {}", icdcResponseEntity);
        if (icdcService.saveIcdcResponse(icdcResponseEntity) == null)
            return new ResponseEntity<>(commonService.responseData("406", "Null entity"), HttpStatus.NOT_ACCEPTABLE);
        return new ResponseEntity<>(commonService.responseData("200", "Response saved!"), HttpStatus.OK);
    }

    @GetMapping("/GetAll")
    public ResponseEntity<List<IcdcEntity>> getAllIcdc(HttpServletRequest request) {
        logger.info(commonService.getHeaders(request).toString());
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if (commonService.authorizationCheck(authKey, token, "get_all")) {
            return ok(icdcService.getAllIcdc());
        }
        if (commonService.authorizationCheck(authKey, token, "icdc")) {
            return ok(icdcService.getAllUserIcdc(token));
        }
        return new ResponseEntity<List<IcdcEntity>>(HttpStatus.UNAUTHORIZED);

    }
//    @GetMapping("/GetAllByUser")
//    public ResponseEntity<List<IcdcEntity>> getAllIcdcByUser(HttpServletRequest request){
//        logger.info(commonService.getHeaders(request).toString());
//        String authKey = request.getHeader("Authorization");
//        String token = request.getHeader("Token");
//        if(!commonService.authorizationCheck(authKey,token,"icdc")){
//            return  new ResponseEntity<List<IcdcEntity>>(HttpStatus.UNAUTHORIZED);
//        }
//        return ok(icdcService.getAllUserIcdc(token));
//    }

    @PutMapping("/Update")
    public ResponseEntity<?> updateIcdc(@RequestBody String params1, HttpServletRequest request) throws JsonProcessingException {
        logger.info("REST API: PUT {} {} Request Headers={}", RequestMappings.APICALLFEATURE, params1 != null ? params1.toString() : "{}",commonService.getHeaders(request));
        if(params1==null)
            return new ResponseEntity<>(commonService.responseData("500","Request must contain parameter values!"),HttpStatus.INTERNAL_SERVER_ERROR);

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if (!commonService.authorizationCheck(authKey, token, "icdc_update")) {
            return new ResponseEntity<List<IcdcEntity>>(HttpStatus.UNAUTHORIZED);
        }
        return icdcService.updateIcdc(params1);
    }

    @DeleteMapping("/Delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");
        if (!commonService.authorizationCheck(authKey, token, "icdc_delete")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        icdcService.deleteIcdc(id);
        return ok(commonService.responseData("200", "ICDC Deleted!"));
    }
}
