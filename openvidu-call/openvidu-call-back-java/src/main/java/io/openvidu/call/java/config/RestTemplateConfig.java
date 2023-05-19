package io.openvidu.call.java.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
  private static final Logger logger= LoggerFactory.getLogger(RestTemplateConfig.class);

  @Value("${http.connectTimeout:10000}")
  private int connectTimeout;

  @Value("${http.readTimeout:10000}")
  private int readTimeout;

  @Value("${http.connectionRequestTimeout:1000}")
  private int connectionRequestTimeout;
  public RestTemplate getRestTemplate() {
      HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
      httpRequestFactory.setConnectionRequestTimeout(connectionRequestTimeout);
      httpRequestFactory.setConnectTimeout(connectTimeout);
      httpRequestFactory.setReadTimeout(readTimeout);
      return new RestTemplate(httpRequestFactory);
  }
}
