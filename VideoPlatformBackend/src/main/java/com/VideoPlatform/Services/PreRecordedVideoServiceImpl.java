package com.VideoPlatform.Services;

import com.VideoPlatform.Entity.AccountEntity;
import com.VideoPlatform.Entity.PreRecordedVideoEntity;
import com.VideoPlatform.Entity.UserAuthEntity;
import com.VideoPlatform.Entity.UserEntity;
import com.VideoPlatform.Repository.PreRecordedVideoRepository;
import com.VideoPlatform.Repository.UserAuthRepository;
import com.VideoPlatform.Repository.UserRepository;
import com.VideoPlatform.Utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

@Service
public class PreRecordedVideoServiceImpl implements PreRecordedVideoService{

    @Value("${file.path}")
    private String FILE_DIRECTORY;

    @Autowired
    private UserAuthRepository userAuthRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommonService commonService;
    @Autowired
    private PreRecordedVideoRepository preRecordedVideoRepository;

    private static final Logger logger= LoggerFactory.getLogger(PreRecordedVideoServiceImpl.class);
    public ResponseEntity<?> addPreRecordedVideo(MultipartFile file, String authKey, String token){

        PreRecordedVideoEntity preRecordedVideoEntity = new PreRecordedVideoEntity();
        if(file.isEmpty()){
            return new ResponseEntity<>(commonService.responseData("400","Request must contain file!"),HttpStatus.BAD_REQUEST);
        }
        try {
            UserAuthEntity userAuthEntity = userAuthRepository.findByToken(token);
            UserEntity userEntity = userRepository.findByUserId(userAuthEntity.getUserId());
            Path path = Paths.get(FILE_DIRECTORY +"/media/"+userEntity.getAccountId()+"/"+userEntity.getUserId()+"/video");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            Files.copy(file.getInputStream(), Paths.get(String.valueOf(path),file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
            logger.info("File Path : {}",path);
            preRecordedVideoEntity.setVideoPath(String.valueOf(path)+"/"+file.getOriginalFilename());
            preRecordedVideoEntity.setVideoName(file.getOriginalFilename());
            preRecordedVideoEntity.setAccountId(userEntity.getAccountId());
            preRecordedVideoEntity.setUserId(userEntity.getUserId());
            Date creation = TimeUtils.getDate();
            preRecordedVideoEntity.setCreationDate(creation);
            preRecordedVideoRepository.save(preRecordedVideoEntity);

        }catch (Exception e){
            logger.error("Exception while uploading file is : {}",e.getMessage());
            return new ResponseEntity<>(commonService.responseData("406","Exception occurred while writing file path!"),HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(commonService.responseData("200","Video Saved!"),HttpStatus.OK);
    }
}
