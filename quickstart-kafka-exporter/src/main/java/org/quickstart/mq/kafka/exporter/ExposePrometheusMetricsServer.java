package org.quickstart.mq.kafka.exporter;

import io.prometheus.client.exporter.MetricsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

class ExposePrometheusMetricsServer implements AutoCloseable {

    private final Server server;

    public ExposePrometheusMetricsServer(int port, MetricsServlet metricsServlet) {
        this.server = new Server(port);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(metricsServlet), "/metrics");
    }

    public void start() {
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
    }
}
