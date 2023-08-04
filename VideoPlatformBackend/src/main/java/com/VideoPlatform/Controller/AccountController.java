package com.VideoPlatform.Controller;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Services.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.VideoPlatform.Constant.RequestMappings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICALLACCOUNT)
public class AccountController {
    private static final Logger logger= LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private CommonService commonService;
    @Autowired
    private AccountService accountService;

    @GetMapping("/GetAll")
    public ResponseEntity<List<AccountEntity>> getAllAccounts(HttpServletRequest request) {
        logger.info(commonService.getHeaders(request).toString());

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"customer_management")){
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<List<AccountEntity>> getAccountById(@PathVariable int id, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"customer_management")){
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PostMapping("/Create")
    public ResponseEntity<?> create(@RequestBody String params1, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        logger.info("REST API: POST {} {} Request Headers={}", RequestMappings.APICALLACCOUNT, params1 != null ? params1 : "{}",commonService.getHeaders(request));
        if(params1==null)
            return new ResponseEntity<>(commonService.responseData("500","Request must contain parameter values!"),HttpStatus.INTERNAL_SERVER_ERROR);

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"customer_creation")){
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!commonService.checkMandatory(params1)){
            return new ResponseEntity<>(commonService.responseData("400","Mandatory parameters are missing!"),HttpStatus.BAD_REQUEST);
        }

        return accountService.accountCreation(params1,authKey,token);
    }

    @PutMapping("/Update")
    public ResponseEntity<?> updateAccount(@RequestBody String params1, HttpServletRequest request){
        logger.info("REST API: PUT {} {} Request Headers={}", RequestMappings.APICALLACCOUNT, params1 != null ? params1 : "{}",commonService.getHeaders(request));
        if(params1==null)
            return new ResponseEntity<>(commonService.responseData("500","Request must contain parameter values!"),HttpStatus.INTERNAL_SERVER_ERROR);

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"customer_update")){
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        return accountService.updateAccount(params1);
    }

    @DeleteMapping("/Delete/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Integer id, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token,"customer_delete")){
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        return accountService.deleteAccount(id);
    }

    @PostMapping("/checkAccountName")
    public ResponseEntity<?> checkAccountName(@RequestBody Map<String, String> params,HttpServletRequest request) {
        String accountName = params.get("accountName");
        return accountService.checkAccountName(accountName);
    }
}
