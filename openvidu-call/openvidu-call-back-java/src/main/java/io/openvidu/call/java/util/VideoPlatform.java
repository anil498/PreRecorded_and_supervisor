package io.openvidu.call.java.util;

import com.google.gson.JsonObject;
import io.openvidu.call.java.models.SessionProperty;
import io.openvidu.java.client.OpenViduHttpException;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.core5.http.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Component
public class VideoPlatform {
  private static final Logger logger = LoggerFactory.getLogger(VideoPlatform.class);
  protected String hostname;
  protected CloseableHttpClient httpClient;
  protected static final String API_PATH = "VPService/v1/Session";
  protected static final String API_FEATURES = "/GetByKey";
  protected static final String API_SESSION = "/sessionDetails";
  protected static final String API_CALLBACK = "/sessionCallback";
  protected static final String API_LINK="/sendLink";

  public VideoPlatform() {

  }

  public VideoPlatform(String hostname) {
    validateHostname(hostname);
    this.hostname = hostname;
    if (!this.hostname.endsWith("/")) {
      this.hostname += "/";
    }
    final HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create().setConnectionTimeToLive(TimeValue.ofSeconds(30)).build();

    RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(300, TimeUnit.SECONDS)
      .setConnectionRequestTimeout(300, TimeUnit.SECONDS).setResponseTimeout(300, TimeUnit.SECONDS).build();

    this.httpClient = HttpClients.custom().setConnectionManager(connectionManager)
      .setDefaultRequestConfig(requestConfig).build();
  }

  public VideoPlatform(String hostname, HttpClientBuilder builder) {
    validateHostname(hostname);
    this.hostname = hostname;
    if (!this.hostname.endsWith("/")) {
      this.hostname += "/";
    }
    this.httpClient = builder.build();
  }

  public SessionProperty getVideoPlatformProperties(String authorization, String token, String sessionKey) throws IOException, HttpException {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", authorization);
    headers.set("Token", token);

    JsonObject json = new JsonObject();
    json.addProperty("sessionKey", sessionKey);
    HttpEntity<String> entity = new HttpEntity<String>(json.toString(), headers);

    String url = this.hostname + API_PATH + API_FEATURES;

    ResponseEntity<SessionProperty> response;
    try {
      response = restTemplate.exchange(url, HttpMethod.POST, entity, SessionProperty.class);
    } catch (RestClientException e) {
      throw new RuntimeException(e.getMessage(), e.getCause());
    }
    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody();
    } else {
      throw new HttpException(String.valueOf(response.getStatusCodeValue()));
    }
  }
  public ResponseEntity<?> sendLink(String authorization, String token, String sessionId) throws HttpException {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", authorization);
    headers.set("Token", token);

    JsonObject json = new JsonObject();
    json.addProperty("sessionId", sessionId);
    HttpEntity<String> entity = new HttpEntity<String>(json.toString(), headers);

    String url = this.hostname + API_PATH + API_LINK;

    ResponseEntity<String> response;
    try {
      response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    } catch (RestClientException e) {
      throw new RuntimeException(e.getMessage(), e.getCause());
    }
    if (response.getStatusCode().is2xxSuccessful()) {
      return response;
    } else {
      throw new HttpException(String.valueOf(response.getStatusCodeValue()));
    }
  }
  protected static IOException HttpException(int status) {
    JsonObject json = new JsonObject();
    json.addProperty("openviduExceptionType", OpenViduHttpException.class.getSimpleName());
    json.addProperty("openviduExceptionMessage", status);
    return new IOException(json.toString());
  }
  private void validateHostname(String hostnameStr) {
    try {
      new URL(hostnameStr);
    } catch (MalformedURLException e) {
      throw new RuntimeException("The hostname \"" + hostnameStr + "\" is not a valid URL: " + e.getMessage());
    }
  }

}
