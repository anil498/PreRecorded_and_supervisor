package io.openvidu.call.java.controllers;

import io.openvidu.call.java.Exception.FileStorageException;
import io.openvidu.call.java.config.FileStorageProperties;
import io.openvidu.call.java.services.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@CrossOrigin(origins = "*")
@RestController
public class FileController {
    public static final Logger logger= LoggerFactory.getLogger(FileController.class);
    @Autowired
    private FileStorageService fileStorageService;
    private final Path fileStorageLocation;

    @Autowired
    public FileController(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
    @Value(("${FILE_PATH:C:\\Users\\jagdeep.khatana\\Downloads\\}"))
    String filePath;

    @GetMapping("/uploadFile")
    public ResponseEntity<?> uploadFile(@RequestParam("file") String fileName) throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            fis = new FileInputStream(filePath+fileName);
            fos = new FileOutputStream(targetLocation.toFile());

            int c;
            while ((c = fis.read()) != -1) {
                fos.write(c);
            }
            logger.info("copied the file successfully");
            return new ResponseEntity<>(HttpStatus.OK);
        }

        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {

            // Closing the streams

            if (fis != null) {

                // Closing the fileInputStream
                fis.close();
            }
            if (fos != null) {

                // Closing the fileOutputStream
                fos.close();
            }
        }
    }
    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
