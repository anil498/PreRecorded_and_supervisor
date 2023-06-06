package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.AccountEntity;
import com.VideoPlatform.Repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public AccountEntity updateAccount(AccountEntity account) {
        logger.info("Account details for update : {} ",account);
        AccountEntity existing = accountRepository.findByAccountId(account.getAccountId());
        logger.info("existing : {}",existing);
        existing.setSession(account.getSession());
        existing.setFeatures(account.getFeatures());
        existing.setFeaturesMeta(account.getFeaturesMeta());
        existing.setAccessId(account.getAccessId());
        existing.setExpDate(account.getExpDate());
        existing.setAddress(account.getAddress());
        existing.setName(account.getName());
        existing.setMaxUser(account.getMaxUser());
        return accountRepository.save(existing);
    }
    @Override
    public String deleteAccount(Integer accountId) {
        accountRepository.deleteAccount(accountId);
        return "Account successfully deleted.";
    }
}
