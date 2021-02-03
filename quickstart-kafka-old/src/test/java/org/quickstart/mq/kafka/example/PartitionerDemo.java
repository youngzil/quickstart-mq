/**
 * 项目名称：quickstart-kafka 
 * 文件名：PartitionerDemo.java
 * 版本信息：
 * 日期：2017年9月25日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.kafka.example;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
 * PartitionerDemo
 * 
 * @author：yangzl
 * @2017年9月25日 下午9:14:14
 * @since 1.0
 */
public class PartitionerDemo implements Partitioner {
    public PartitionerDemo(VerifiableProperties props) {

    }

    @Override
    public int partition(Object obj, int numPartitions) {
        int partition = 0;
        if (obj instanceof String) {
            String key = (String) obj;
            int offset = key.lastIndexOf('.');
            if (offset > 0) {
                partition = Integer.parseInt(key.substring(offset + 1)) % numPartitions;
            }
        } else {
            partition = obj.toString().length() % numPartitions;
        }

        return partition;
    }

}
