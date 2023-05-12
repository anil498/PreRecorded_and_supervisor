package com.openvidu_databases.openvidu_dbbackend.Services;

import com.openvidu_databases.openvidu_dbbackend.Controller.UserController;
import com.openvidu_databases.openvidu_dbbackend.Entity.AccountEntity;
import com.openvidu_databases.openvidu_dbbackend.Entity.UserEntity;
import com.openvidu_databases.openvidu_dbbackend.Repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    @Autowired
    AccountRepository accountRepository;

    private static final Logger logger= LoggerFactory.getLogger(AccountService.class);


    public List<AccountEntity> getAllAccounts() {
        //logger.info(String.valueOf(userRepository.findAll()));
        return accountRepository.findAll();
    }
    public List<AccountEntity> getAccountById(int id) {
        //logger.info(String.valueOf(userRepository.findById(id)));
        return accountRepository.findById(id);
    }

    public AccountEntity createAccount(AccountEntity account) {
        //logger.info("User details {}",account.toString());
        // user.setUserPassword(user.getUserPassword());

        return accountRepository.save(account);
    }
}
