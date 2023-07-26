package io.openvidu.call.java.controllers;

import io.openvidu.call.java.services.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class FileController {
    public static final Logger logger= LoggerFactory.getLogger(FileController.class);
    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/downloadFile")
    public ResponseEntity<?> downloadFile(@RequestBody(required = false) Map<String, Object> params, HttpServletRequest request) {
        // Load file as Resource
        logger.info("Request API /downloadFile Parameters {}",params);
        String fileName;
        if(params==null){
            return  ResponseEntity.internalServerError().body("Body not found");
        }
        if (params.containsKey("fileName")) {
            fileName = params.get("fileName").toString();
            Resource resource = fileStorageService.loadFileAsResource(fileName);

            // Try to determine file's content type
            String contentType = null;

            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                logger.info("Could not determine file type.");
            }

            // Fallback to the default content type if type could not be determined
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        }else {
            return ResponseEntity.internalServerError().body("File name not found");
        }
    }
}
