package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.UserEntity;
import com.VideoPlatform.Repository.UserRepository;
import com.VideoPlatform.Repository.AccountRepository;
import com.VideoPlatform.Repository.SessionRepository;
import com.VideoPlatform.Repository.UserAuthRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAuthRepository userAuthRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SessionRepository sessionRepository;

    private static final Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);
    @Override
    public List<UserEntity> getAllUsers() {
        //logger.info(String.valueOf(userRepository.findAll()));
        return userRepository.findAll();
    }

    @Override
    public List<UserEntity> getAllChild(Integer id) {
         return userRepository.findAllChild(id);
    }

    @Override
    public UserEntity getUserById(Integer id) {
        //logger.info(String.valueOf(userRepository.findById(id)));
        return (UserEntity) userRepository.findByUserId(id);
    }

    @Override
    public UserEntity createUser(UserEntity user) {
        logger.info("User details {}",user.toString());
       // user.setUserPassword(user.getUserPassword());

        return userRepository.save(user);
    }

    @Override
    public UserEntity updateUser(UserEntity user) {
        UserEntity existing = (UserEntity) userRepository.findByUserId(user.getUserId());
        existing.setFname(user.getFname());
        existing.setLname(user.getLname());
        existing.setSession(user.getSession());
        existing.setFeatures(user.getFeatures());
        existing.setFeaturesMeta(user.getFeaturesMeta());
        existing.setAccessId(user.getAccessId());
        existing.setExpDate(user.getExpDate());
        existing.setContact(user.getContact());
        existing.setEmail(user.getEmail());
        return userRepository.save(existing);
    }

    @Override
    public String deleteUser(Integer userId) {
        userRepository.deleteUser(userId);
        return "User successfully deleted.";
    }



}

