package io.openvidu.call.java.services;

import io.openvidu.call.java.Exception.MyFileNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
@Service
public class FileStorageService {

    public Resource loadFileAsResource(String filePath) {
        try {
            Path fullPath = Paths.get(filePath).toAbsolutePath().normalize();
            Resource resource = new UrlResource(fullPath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + filePath);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + filePath, ex);
        }
    }
}
