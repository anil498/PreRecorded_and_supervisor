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
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import io.grpc.netty.shaded.io.netty.handler.timeout.ReadTimeoutException;
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

  public SessionRequest getVideoPlatformProperties(String authorization, String token,String sessionKey) throws IOException {
    HttpClientResponseHandler<SessionRequest> responseHandler = new HttpClientResponseHandler<SessionRequest>() {
      public SessionRequest handleResponse(ClassicHttpResponse response) throws IOException, HttpException {
        int status = response.getCode();
        if (status == 200) {
          return new SessionRequest(response.getEntity());
        } else {
          throw HttpException(status);
        }
      }
    };
    JsonObject json = new JsonObject();
    json.addProperty("sessionKey",sessionKey);
    StringEntity params = new StringEntity(json.toString(), StandardCharsets.UTF_8);
    HttpPost request = new HttpPost(this.hostname + API_PATH + API_FEATURES);
    request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    request.setHeader("Authorization", authorization);
    request.setHeader("Token", token);
    request.setEntity(params);
    CloseableHttpResponse response = null;
    try {
      response = this.httpClient.execute(request);
      return responseHandler.handleResponse(response);
    } catch (IOException | HttpException e) {
      throw new RuntimeException(e.getMessage(), e.getCause());
    } finally {
      if (response != null) {
        response.close();
      }
    }
  }

  public HashMap<String, Integer> getExpiredTimer() throws IOException {
    HttpClientResponseHandler<HashMap<String, Integer>> responseHandler = new HttpClientResponseHandler<HashMap<String, Integer>>() {
      public HashMap<String, Integer> handleResponse(ClassicHttpResponse response) throws IOException, HttpException {
        int status = response.getCode();
        if (status == 200) {
          ObjectMapper objectMapper = new ObjectMapper();
          JsonObject json = httpResponseEntityToJson(response.getEntity());
          HashMap<String, Integer> sessionIds = objectMapper.readValue(json.getAsString(), HashMap.class);
          return sessionIds;
        } else {
          throw HttpException(status);
        }
      }
    };
//    JsonObject json = new JsonObject();
//    json.addProperty("accountId", accountId);
//    json.addProperty("userId", userId);
//    StringEntity params = new StringEntity(json.toString(), StandardCharsets.UTF_8);
    HttpPost request = new HttpPost(this.hostname + API_PATH + API_FEATURES);
    request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
//    request.setEntity(params);
    CloseableHttpResponse response = null;
    try {
      response = this.httpClient.execute(request);
      return responseHandler.handleResponse(response);
    } catch (IOException | HttpException e) {
      throw new RuntimeException(e.getMessage(), e.getCause());
    } finally {
      if (response != null) {
        response.close();
      }
    }
  }

  public boolean sendSessionCallback(SessionCallback sessionCallback, int callbackRetryAttempts) throws IOException {
    StringEntity params = new StringEntity(sessionCallback.toString(), StandardCharsets.UTF_8);
    HttpPost request = new HttpPost(this.hostname + API_PATH + API_CALLBACK);
    request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    request.setEntity(params);

    logger.info("Going to call callback url [{}] with json [{}]", this.hostname + API_PATH + API_CALLBACK, sessionCallback);

    RetryConfig retryConfig = RetryConfig.custom().maxAttempts(callbackRetryAttempts).build();
    Retry retry = Retry.of("callbackRetry", retryConfig);
    try(CloseableHttpResponse response = retry.executeCallable(() -> httpClient.execute(request))) {
      int statusCode = response.getCode();
      logger.info("Response code: {}", statusCode);

      if (statusCode == HttpStatus.SC_OK) {
        String responseBody = EntityUtils.toString(response.getEntity());
        logger.info("Response body: {}", responseBody);
        return true;
      } else {
        logger.error("Getting error {}", statusCode);
      }
    } catch (HttpHostConnectException e) {
      logger.error("VideoPlatform is not accessible {}", e.getMessage());
      return false;
    }
      catch (ResourceAccessException e)
      {
        logger.error("VideoPlatform is not accessible {}",e.getMessage());
        return false;
      }
			catch (ReadTimeoutException e)
      {
        logger.error("VideoPlatform is not accessible {} ",e.getMessage());
        return false;
      }
     catch (IOException e) {
      logger.error("Error in calling callback for sessionId [{}]", sessionCallback.getUniqueSessionId(), e);
    } catch (Throwable t) {
      logger.error("Error in calling callback for sessionId [{}]", sessionCallback.getUniqueSessionId(), t);
    }
    return true;
  }
  protected static JsonObject httpResponseEntityToJson(HttpEntity responseEntity) throws IOException {
    try {
      JsonObject json = new Gson().fromJson(EntityUtils.toString(responseEntity, StandardCharsets.UTF_8),
        JsonObject.class);
      return json;
    } catch (JsonSyntaxException | ParseException | IOException e) {
      JsonObject json = new JsonObject();
      json.addProperty("openviduExceptionType", OpenViduJavaClientException.class.getSimpleName());
      json.addProperty("openviduExceptionMessage", e.getMessage());
      throw new IOException(json.toString(), e.getCause());
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
