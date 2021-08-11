package org.quickstart.mq.kafka.sample;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.izettle.metrics.influxdb.InfluxDbHttpSender;
import com.izettle.metrics.influxdb.InfluxDbReporter;
import com.izettle.metrics.influxdb.InfluxDbSender;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MetricsReporter {

    private String reporterHost = "127.0.0.1";
    private int reporterPort = 8086;
    private int reportIntervalMs=5000;

    private String reportDatabase = "testkafka";

    @Getter
    private MetricRegistry registry;

    private InfluxDbSender influxDbHttpSender;
    private InfluxDbReporter influxDbReporter;
    String measurementPrefix = "kafka_";

    public void init() {

        Map<String, String> tags = new HashMap<>();
        tags.put("HOST", "127.0.0.1");
        registry = new MetricRegistry();
        try {
            influxDbHttpSender = new InfluxDbHttpSender("http",//
                reporterHost,//
                reporterPort,//
                reportDatabase,//
                "",//
                TimeUnit.SECONDS,//
                5000,//
                5000,//
                measurementPrefix);

            influxDbReporter = InfluxDbReporter.forRegistry(registry)//
                .convertRatesTo(TimeUnit.SECONDS)//
                .convertDurationsTo(TimeUnit.MILLISECONDS)//
                .filter(MetricFilter.ALL)//
                .skipIdleMetrics(false)//
                .withTags(tags)//
                .build(influxDbHttpSender);

            influxDbReporter.start(reportIntervalMs, TimeUnit.MILLISECONDS);

        } catch (Exception e) {
        }

    }

}

