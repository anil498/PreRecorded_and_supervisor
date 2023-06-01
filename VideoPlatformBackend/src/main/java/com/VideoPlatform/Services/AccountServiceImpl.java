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
        //logger.info(String.valueOf(userRepository.findAll()));
        return accountRepository.findAll();
    }
    @Override
    public List<AccountEntity> getAccountById(int id) {
        //logger.info(String.valueOf(userRepository.findById(id)));
        return accountRepository.findById(id);
    }
    @Override
    public AccountEntity createAccount(AccountEntity account) {
        //logger.info("User details {}",account.toString());
        // user.setUserPassword(user.getUserPassword());

        return accountRepository.save(account);
    }
}
