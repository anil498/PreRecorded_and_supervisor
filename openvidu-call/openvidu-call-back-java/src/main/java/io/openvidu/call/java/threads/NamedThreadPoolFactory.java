package io.openvidu.call.java.threads;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadPoolFactory implements ThreadFactory {
  private ThreadGroup group;
  private final AtomicInteger threadNumber = new AtomicInteger(1);
  private String namePrefix;
  private int priority;

  public NamedThreadPoolFactory(String name) {
    SecurityManager s = System.getSecurityManager();
    group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
    namePrefix = name + "-thread-";
    priority = Thread.NORM_PRIORITY;
  }

  public NamedThreadPoolFactory(String poolName, int Priority) {
    SecurityManager s = System.getSecurityManager();
    group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
    namePrefix = poolName + "-thread-";
    priority = Priority;
  }

  @Override
  public Thread newThread(Runnable r) {
    Thread thread = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
    thread.setDaemon(true);
    thread.setPriority(priority);
    return thread;
  }
}
