# --- Build ---
FROM eclipse-temurin:25-jdk AS build

ENV NODE_VERSION=24.15.0
ENV NVM_DIR=/root/.nvm

# Install Node
RUN apt-get update \
	  && apt-get install -y \
          curl \
    && apt-get clean autoclean \
    && apt-get autoremove --yes \
    && rm -rf /var/lib/apt/lists/*

RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.4/install.sh | bash
RUN . "$NVM_DIR/nvm.sh" && nvm install ${NODE_VERSION}
RUN . "$NVM_DIR/nvm.sh" && nvm use v${NODE_VERSION}
RUN . "$NVM_DIR/nvm.sh" && nvm alias default v${NODE_VERSION}
ENV PATH="/root/.nvm/versions/node/v${NODE_VERSION}/bin/:${PATH}"

# Copy files
WORKDIR /app

COPY ./build.gradle /app
COPY ./gradle.properties /app
COPY ./settings.gradle /app
COPY ./gradle /app/gradle
COPY ./gradlew /app
COPY ./frontend /app/frontend
COPY ./backend /app/backend
COPY ./openapi /app/openapi

# Build
RUN chmod 555 ./gradlew \
#    fix CRLF line endings in gradlew
    && sed -i -e 's/\r$//' ./gradlew \
    && ./gradlew install -x :backend:test

# --- Custom minimal JRE via jlink ---
FROM eclipse-temurin:25-jdk AS jre-build

COPY --from=build /app/backend/build/libs/app.jar /app.jar
RUN jlink \
      --add-modules java.base,java.compiler,java.desktop,java.instrument,java.management,java.naming,java.net.http,java.prefs,java.rmi,java.scripting,java.security.jgss,java.security.sasl,java.sql,java.sql.rowset,java.xml,java.xml.crypto,jdk.attach,jdk.crypto.ec,jdk.httpserver,jdk.jfr,jdk.management,jdk.naming.dns,jdk.naming.rmi,jdk.net,jdk.unsupported,jdk.unsupported.desktop \
      --strip-debug --no-man-pages --no-header-files \
      --compress=zip-9 --output /jre

# --- Runtime image ---
FROM cm2network/steamcmd AS runtime

COPY --from=jre-build /jre /opt/java/openjdk
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# The container starts as root so the entrypoint can remap the steam user to
# the operator's uid/gid and migrate mounted data ownership, then drops to
# the steam user via gosu before launching the JVM.
USER root
RUN dpkg --add-architecture i386 \
    && apt-get update \
    && apt-get install -y --no-install-recommends \
          lib32gcc-s1 \
          lib32stdc++6 \
          libcap2 \
          expect \
          gosu \
          libtbbmalloc2 \
    && apt-get clean autoclean \
    && apt-get autoremove --yes \
    && rm -rf /var/lib/apt/lists/* /var/cache/apt/archives/*

WORKDIR /home/steam
COPY ./config/application-docker.properties /home/steam/config/application.properties
COPY --from=build /app/backend/build/libs/app.jar /home/steam/app.jar
COPY ./docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

RUN chown -R steam:steam /home/steam \
    && chmod -R 755 /home/steam \
    && chmod +x /usr/local/bin/docker-entrypoint.sh

ENV LANG="en_US.UTF-8"
ENV LANGUAGE="en_US.UTF-8"
ENV LC_ALL="en_US.UTF-8"

ENV STEAMCMD_PATH=/home/steam/steamcmd/steamcmd.sh
ENV DIRECTORY_SERVERS=/home/steam/armaservermanager/servers
ENV DIRECTORY_MODS=/home/steam/armaservermanager/mods
ENV DIRECTORY_LOGS=/home/steam/armaservermanager/logs

EXPOSE 8080/tcp
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
CMD ["java", "-jar", "./app.jar"]
