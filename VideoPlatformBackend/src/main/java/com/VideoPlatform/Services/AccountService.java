package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.AccountEntity;

import java.util.List;

public interface AccountService {
    List<AccountEntity> getAllAccounts();
    List<AccountEntity> getAccountById(int id);
    AccountEntity createAccount(AccountEntity account);
}
