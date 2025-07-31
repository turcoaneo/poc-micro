FROM apache/kafka:latest

# Set working directory for consistency
WORKDIR /opt/kafka

# Copy custom server config
COPY server.properties config/server.properties

# Create certs directory if it doesn't exist and copy certs into container
RUN mkdir -p certs
COPY cert-uat/*.jks certs/

RUN cp /opt/kafka/certs/*.jks /tmp/ && \
    rm /opt/kafka/certs/*.jks && \
    mv /tmp/*.jks /opt/kafka/certs/ && \
    chmod 644 /opt/kafka/certs/*.jks

# Format storage with dynamically generated cluster ID
RUN CLUSTER_ID=$(/opt/kafka/bin/kafka-storage.sh random-uuid) && \
    /opt/kafka/bin/kafka-storage.sh format -t "$CLUSTER_ID" -c config/server.properties

# Start Kafka broker
ENTRYPOINT ["/opt/kafka/bin/kafka-server-start.sh"]
CMD ["config/server.properties"]