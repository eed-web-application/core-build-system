FROM eclipse-temurin:21-jammy

# Create the app user and group

WORKDIR /opt/app

# Copy files
COPY ./build/libs/*-plain.jar /opt/app/app-plain.jar
COPY ./build/libs/*.jar /opt/app/app.jar

# Change ownership and permissions
RUN chmod 755 /opt/app/*.jar

# Add wait script
ENV WAIT_VERSION 2.7.2
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/$WAIT_VERSION/wait /opt/app/wait
RUN chmod 755 /opt/app/wait

# Switch to non-root user by default
EXPOSE 8080

ENTRYPOINT ["/bin/sh", "-c", "/opt/app/wait && exec java -jar /opt/app/app.jar"]
