package com.openvidu_databases.openvidu_dbbackend.Services;

import com.openvidu_databases.openvidu_dbbackend.Entity.AccountEntity;
import com.openvidu_databases.openvidu_dbbackend.Entity.SessionEntity;
import com.openvidu_databases.openvidu_dbbackend.Entity.UserEntity;
import com.openvidu_databases.openvidu_dbbackend.Repository.AccountRepository;
import com.openvidu_databases.openvidu_dbbackend.Repository.SessionRepository;
import com.openvidu_databases.openvidu_dbbackend.Repository.UserAuthRepository;
import com.openvidu_databases.openvidu_dbbackend.Repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAuthRepository userAuthRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SessionRepository sessionRepository;

    private static final Logger logger= LoggerFactory.getLogger(UserService.class);
    public List<UserEntity> getAllUsers() {
        //logger.info(String.valueOf(userRepository.findAll()));
        return userRepository.findAll();
    }

    public List<UserEntity> getAllChild(Integer id) {
         return userRepository.findAllChild(id);
    }

    public UserEntity getUserById(Integer id) {
        //logger.info(String.valueOf(userRepository.findById(id)));
        return (UserEntity) userRepository.findByUserId(id);
    }

    public UserEntity createUser(UserEntity user) {
        logger.info("User details {}",user.toString());
       // user.setUserPassword(user.getUserPassword());

        return userRepository.save(user);
    }

    public UserEntity updateUser(UserEntity user, Integer id) {
        UserEntity existing = (UserEntity) userRepository.findByUserId(id);
        existing.setEmail(user.getEmail());
        existing.setContact(user.getContact());
        existing.setFname(user.getFname());
        existing.setLname(user.getLname());
        return userRepository.save(existing);
    }

    public String deleteUser(Integer userId) {
        userRepository.deleteById(userId);
        return "User successfully deleted.";
    }



}

