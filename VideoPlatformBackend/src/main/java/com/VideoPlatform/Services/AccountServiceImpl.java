package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.AccountEntity;
import com.VideoPlatform.Repository.AccountRepository;
import com.VideoPlatform.Utils.TimeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    private static final Logger logger= LoggerFactory.getLogger(AccountServiceImpl.class);

    @Override
    public List<AccountEntity> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public List<AccountEntity> getAccountById(int id) {
        return accountRepository.findById(id);
    }

    @Override
    public AccountEntity createAccount(AccountEntity account) {

        return accountRepository.save(account);
    }

    @Override
    public AccountEntity updateAccount(String params1) {
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(params1,JsonObject.class);
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        AccountEntity existing = accountRepository.findByAccountId(params.get("accountId").getAsInt());
        try {
            existing.setLogo(objectMapper.readValue(params.get("logo").toString(), HashMap.class));
            existing.setSession(objectMapper.readValue(params.get("session").toString(),HashMap.class));
            existing.setFeaturesMeta(objectMapper.readValue(params.get("featuresMeta").toString(),HashMap.class));
            existing.setAccessId(objectMapper.readValue(params.get("accessId").toString(),Integer[].class));
            existing.setFeatures(objectMapper.readValue(params.get("features").toString(),Integer[].class));
            Date expDate = TimeUtils.parseDate(objectMapper.readValue(params.get("expDate").toString(),String.class));
            existing.setExpDate(expDate);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        existing.setMaxUser(params.get("maxUser").getAsInt());
        existing.setName(params.get("name").getAsString());
        existing.setAddress(params.get("address").getAsString());
        logger.info("New Entity {}",existing);
        return accountRepository.save(existing);
    }
    @Override
    public String deleteAccount(Integer accountId) {
        accountRepository.deleteAccount(accountId);
        return "Account successfully deleted.";
    }
}
