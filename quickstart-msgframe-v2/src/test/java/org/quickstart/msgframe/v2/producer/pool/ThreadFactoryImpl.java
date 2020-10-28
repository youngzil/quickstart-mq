/**
 * 项目名称：quickstart-mq-msgframe-v2 
 * 文件名：ThreadFactoryImpl.java
 * 版本信息：
 * 日期：2018年7月3日
 * Copyright youngzil Corporation 2018
 * 版权所有 *
 */
package org.quickstart.msgframe.v2.producer.pool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ThreadFactoryImpl
 * 
 * @author：yangzl
 * @2018年7月3日 下午2:47:58
 * @since 1.0
 */
public class ThreadFactoryImpl implements ThreadFactory {

    private final AtomicLong threadIndex = new AtomicLong(0);
    private final String threadNamePrefix;

    public ThreadFactoryImpl(final String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, threadNamePrefix + this.threadIndex.incrementAndGet());

    }
}
