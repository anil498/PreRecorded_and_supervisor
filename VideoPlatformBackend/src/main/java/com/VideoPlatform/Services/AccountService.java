package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.AccountEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AccountService {
    List<AccountEntity> getAllAccounts();
    List<AccountEntity> getAccountById(int id);
    ResponseEntity<?> accountCreation(String params1, String authKey, String token) throws JsonProcessingException;
    AccountEntity createAccount(AccountEntity account);
    ResponseEntity<?> updateAccount(String params1);
    ResponseEntity<?> deleteAccount(Integer accountId);
    ResponseEntity<?> checkAccountName(String accountName);
    void saveFilePathToFeature(String filePath, String loginId, String name);
}
