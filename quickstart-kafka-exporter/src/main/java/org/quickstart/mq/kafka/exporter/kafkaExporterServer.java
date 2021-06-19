package org.quickstart.mq.kafka.exporter;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import io.prometheus.client.exporter.MetricsServlet;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class kafkaExporterServer {

    @Parameter(names = "--kafka-host", description = "Kafka hostname", required = true)
    public String kafkaHostname;

    @Parameter(names = "--kafka-port", description = "Kafka port")
    public int kafkaPort = 9092;

    @Parameter(names = "--port", description = "Exporter port")
    public int port = 7979;

    @Parameter(names = "--group-blacklist-regexp", description = "Consumer group blacklist regexp")
    public String groupBlacklistRegexp = "console-consumer.*";

    @Parameter(names = "--scrape-period", description = "Scrape period")
    public int scrapePeriod = 30;

    @Parameter(names = "--scrape-period-unit", description = "Scrape period timeunit")
    public TimeUnit scrapePeriodUnit = TimeUnit.SECONDS;

    @Parameter(names = "--help", help = true)
    private boolean help = false;

    public static void main(String... args) throws Exception {
        kafkaExporterServer main = new kafkaExporterServer();
        JCommander jcommander = JCommander.newBuilder()
                .addObject(main)
                .build();

        jcommander.parse(args);

        if (main.help) {
            jcommander.usage();
        } else {
            KafkaExporter
                kafkaExporter = new KafkaExporter(main.kafkaHostname, main.kafkaPort, main.groupBlacklistRegexp);

            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    kafkaExporter.updateMetrics();
                }
            }, 0, main.scrapePeriodUnit.toMillis(main.scrapePeriod));

            ExposePrometheusMetricsServer prometheusMetricServlet = new ExposePrometheusMetricsServer(main.port, new MetricsServlet());
            prometheusMetricServlet.start();
        }
    }

}
