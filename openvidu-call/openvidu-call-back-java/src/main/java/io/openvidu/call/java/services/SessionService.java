package io.openvidu.call.java.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.openvidu.java.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SessionService {
  @Autowired
  VideoPlatformService videoPlatformService;
  @Value(("${MEDIA_PATH:/opt/openvidu/kurento-logs/prerecorded/}"))
  String mediaPath;
  private static final Logger logger = LoggerFactory.getLogger(SessionService.class);

  public void autoPlay(Session session, String cameraUri, String cameraName) throws OpenViduJavaClientException, OpenViduHttpException {
    session.fetch();
    List<String> alreadyPublishedCameras = session.getActiveConnections().stream()
      .filter(connection -> "IPCAM".equals(connection.getPlatform()))
      .map(connection -> connection.getServerData()).collect(Collectors.toList());
    try {
      // Publish the camera only if it is not already published
      if (!alreadyPublishedCameras.contains(cameraName)) {

        File file = new File(cameraUri);
        String fileName = file.getName();
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder()
          .type(ConnectionType.IPCAM)
          .rtspUri("file://"+mediaPath+fileName)
          .adaptativeBitrate(true)
          .onlyPlayWithSubscribers(true).data("{\"clientData\":\"Prerecorded_video\",\"type\":\"SCREEN\"}")
          .build();
        session.createConnection(connectionProperties);
        logger.info("Connection properties {}",connectionProperties.getRtspUri());
      }
    } catch (Exception e) {
      logger.error("Error publishing camera {} {}", session.getSessionId(),e);
    }
  }

  public String convertByteToBase64(Object logo) {
    String base64Logo =null;
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      String logoJson = objectMapper.writeValueAsString(logo);

      JsonNode logoNode = objectMapper.readTree(logoJson);

      String byteValue = logoNode.get("byte").asText();
      String type = logoNode.get("type").toString();

      byte[] logoByte = Base64.getDecoder().decode(byteValue);

      base64Logo="data:image/" + type + ";base64," + Base64.getEncoder().encodeToString(logoByte);
    } catch (Exception e) {
      logger.error("Getting error while converting to base64 {}", e);
    }
    return base64Logo;
  }
}
