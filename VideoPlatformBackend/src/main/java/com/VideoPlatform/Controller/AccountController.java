package com.VideoPlatform.Controller;

import com.VideoPlatform.Entity.*;
import com.VideoPlatform.Repository.*;
import com.VideoPlatform.Services.*;
import com.VideoPlatform.Utils.TimeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.VideoPlatform.Constant.RequestMappings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(RequestMappings.APICALLACCOUNT)
public class AccountController {
    private static final Logger logger= LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private AccountService accountService;

    @Value("${secret.key}")
    private String secret;
    @Value("${access.time}")
    private int accessTime;

    @GetMapping("/GetAll")
    public ResponseEntity<List<AccountEntity>> getAllAccounts(HttpServletRequest request) {
        logger.info(commonService.getHeaders(request).toString());

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token)){
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!(commonService.checkAccess("customer_management",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<List<AccountEntity>> getAccountById(@PathVariable int id, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token)){
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!(commonService.checkAccess("customer_management",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PostMapping("/Create")
    public ResponseEntity<?> create(@RequestBody String params1, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token)){
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!(commonService.checkAccess("customer_creation",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        accountService.accountCreation(params1,authKey,token);

        Map<String,String> result = new HashMap<>();
        result.put("status_code ","200");
        result.put("msg", "Account created!");

        return new ResponseEntity<>(result,HttpStatus.CREATED);
    }

    @PutMapping("/Update")
    public ResponseEntity<?> updateAccount(@RequestBody String params1, HttpServletRequest request){

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token)){
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if(!(commonService.checkAccess("customer_update",token))){
            logger.info("Permission Denied. Don't have access for this service!");
            return  new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        accountService.updateAccount(params1);
        Map<String,String> result = new HashMap<>();
        result.put("status_code ","200");
        result.put("msg", "Account updated!");
        return ok(result);
    }

    @DeleteMapping("/Delete/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Integer id, HttpServletRequest request) {

        String authKey = request.getHeader("Authorization");
        String token = request.getHeader("Token");

        if(!commonService.authorizationCheck(authKey,token)){
            return  new ResponseEntity<List<AccountEntity>>(HttpStatus.UNAUTHORIZED);
        }
        if (!(commonService.checkAccess("customer_delete", token))) {
            logger.info("Permission Denied. Don't have access for this service!");
            return new ResponseEntity<AccountEntity>(HttpStatus.UNAUTHORIZED);
        }
        accountService.deleteAccount(id);

        Map<String,String> result = new HashMap<>();
        result.put("status_code ","200");
        result.put("msg", "Account deleted!");

        return ok(result);
    }
}
