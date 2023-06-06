package io.openvidu.call.java.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.openvidu.call.java.models.SessionCallback;
import io.openvidu.call.java.models.SessionRequest;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import org.apache.hc.client5.http.HttpHostConnectException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.core5.http.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import io.grpc.netty.shaded.io.netty.handler.timeout.ReadTimeoutException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class VideoPlatform {
  private static final Logger logger = LoggerFactory.getLogger(VideoPlatform.class);
  protected String hostname;
  protected CloseableHttpClient httpClient;
  protected static final String API_PATH = "VPService/v1/Session";
  protected static final String API_FEATURES = "/GetByKey";
  protected static final String API_SESSION = "/sessionDetails";
  protected static final String API_CALLBACK = "/sessionCallback";

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

  public SessionRequest getVideoPlatformProperties(String authorization, String token, String sessionKey) throws IOException, HttpException {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", authorization);
    headers.set("Token", token);

    JsonObject json = new JsonObject();
    json.addProperty("sessionKey", sessionKey);
    HttpEntity<String> entity = new HttpEntity<String>(json.toString(), headers);

    String url = this.hostname + API_PATH + API_FEATURES;

    ResponseEntity<SessionRequest> response;
    try {
      response = restTemplate.exchange(url, HttpMethod.POST, entity, SessionRequest.class);
    } catch (RestClientException e) {
      throw new RuntimeException(e.getMessage(), e.getCause());
    }

    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody();
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
