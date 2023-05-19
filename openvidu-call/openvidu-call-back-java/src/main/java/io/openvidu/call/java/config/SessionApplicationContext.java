package io.openvidu.call.java.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SessionApplicationContext implements ApplicationContextAware {
  private static ApplicationContext context;
  private static final Logger logger  = LoggerFactory.getLogger(SessionApplicationContext.class);

  public SessionApplicationContext() {
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    context=applicationContext;
  }
  public static <T> T getBean(Class<T> cClass) {
    return context.getBean(cClass);
  }

  public static <T> T getBean(String beanName, Class<T> cClass) {
    return context.getBean(beanName, cClass);
  }

  public static <T> T getBean(String beanName) {
    return (T) context.getBean(beanName);
  }
}
