FROM eclipse-temurin:21-jammy

# Create the app user and group
RUN useradd -rm -d /home/app -s /bin/bash -g root -G sudo -u 1001 app

WORKDIR /home/app

# Copy files
COPY ./tools/run.sh /home/app
COPY ./build/libs/*-plain.jar /home/app/app-plain.jar
COPY ./build/libs/*.jar /home/app/app.jar

# Change ownership and permissions
RUN chown -R app:root /home/app \
    && chmod 755 /home/app/*.jar \
    && chmod 755 /home/app/run.sh

# Add wait script
ENV WAIT_VERSION 2.7.2
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/$WAIT_VERSION/wait /home/app/wait
RUN chmod 755 /home/app/wait

# Switch to non-root user by default
USER app
EXPOSE 8080

ENTRYPOINT ["/bin/sh", "-c", "/home/app/wait && exec java -jar /home/app/app.jar"]
