package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.AccountEntity;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface AccountService {
    List<AccountEntity> getAllAccounts();
    List<AccountEntity> getAccountById(int id);
    String accountCreation(String params1,String authKey,String token) throws JsonProcessingException;
    AccountEntity createAccount(AccountEntity account);
    AccountEntity updateAccount(String params1);
    String deleteAccount(Integer accountId);
}
