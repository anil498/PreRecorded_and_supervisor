package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.AccountAuthEntity;
import com.VideoPlatform.Entity.UserAuthEntity;
import com.VideoPlatform.Entity.UserEntity;
import com.VideoPlatform.Repository.*;

import com.VideoPlatform.Utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static com.VideoPlatform.Constant.AllConstants.DATE_FORMATTER;

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
    @Autowired
    private AccountAuthRepository accountAuthRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

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
    public UserEntity createUser(UserEntity user,String authKey,String token) {
        logger.info("User details {}",user.toString());
        AccountAuthEntity acc = accountAuthRepository.findByAuthKey(authKey);
        UserAuthEntity u = userAuthRepository.findByToken(token);
        Date creation = CommonUtils.getDate();
        user.setAccountId(acc.getAccountId());
        user.setCreationDate(creation);
        user.setParentId(u.getUserId());
        String mypass = passwordEncoder.encode(user.getPassword());
        user.setPassword(mypass);

        return userRepository.save(user);
    }
    @Override
    public UserEntity createUserZero(UserEntity user) {
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
        existing.setParentId(user.getParentId());
        existing.setPassword(user.getPassword());
        existing.setUserId(user.getUserId());
        existing.setAccountId(user.getAccountId());
        existing.setLoginId(user.getLoginId());
        existing.setLastLogin(user.getLastLogin());
        existing.setCreationDate(user.getCreationDate());

        return userRepository.save(existing);
    }

    @Override
    public String deleteUser(Integer userId) {
        userRepository.deleteUser(userId);
        return "User successfully deleted.";
    }
}

