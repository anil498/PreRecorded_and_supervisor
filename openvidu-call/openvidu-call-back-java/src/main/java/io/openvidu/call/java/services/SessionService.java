package io.openvidu.call.java.services;

import io.openvidu.java.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {
  @Autowired
  VideoPlatformService videoPlatformService;
  private static final Logger logger= LoggerFactory.getLogger(SessionService.class);
  public void autoPlay(Session session, String cameraUri, String cameraName) throws OpenViduJavaClientException, OpenViduHttpException {
    session.fetch();
    List<String> alreadyPublishedCameras = session.getActiveConnections().stream()
      .filter(connection -> "IPCAM".equals(connection.getPlatform()))
      .map(connection -> connection.getServerData()).collect(Collectors.toList());
    try {
      // Publish the camera only if it is not already published
      if (!alreadyPublishedCameras.contains(cameraName)) {
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder()
          .type(ConnectionType.IPCAM)
          .data(cameraName)
          .rtspUri(cameraUri)
          .adaptativeBitrate(true)
          .onlyPlayWithSubscribers(true)
          .build();
        session.createConnection(connectionProperties);
      }
    } catch (Exception e) {
      logger.error("Error publishing camera {}", session.getSessionId());
    }
  }
  public void writeLogoFile(byte[] file,String fileName,String filePath){
    try {
      Path path = Path.of(filePath+fileName);
      Files.write(path, file, StandardOpenOption.CREATE);
      logger.info("File written successfully at: {}",path);
    } catch (Exception e) {
      logger.error("Getting error while writing file {}",e);
    }
  }
}
