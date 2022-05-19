FROM influxdb:2.2.0-alpine
USER root
RUN chown 1000:1000 /etc/influxdb2 && \
    chown -R 1000:1000 /etc/influxdb2 && \
    chmod -R a+rwx /etc/influxdb2 && \
    chown 1000:1000 /var/lib/influxdb2 && \
    chown -R 1000:1000 /var/lib/influxdb2 && \
    chmod -R a+rwx /var/lib/influxdb2

USER 1000
