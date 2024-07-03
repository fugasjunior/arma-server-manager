# --- Build ---
FROM eclipse-temurin:17-jdk AS build

ENV NODE_VERSION=20.15.0
ENV NVM_DIR=/root/.nvm

# Install Node
RUN apt-get update \
	  && apt-get install -y \
          curl \
    && apt-get clean autoclean \
    && apt-get autoremove --yes \
    && rm -rf /var/lib/apt/lists/*

RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
RUN . "$NVM_DIR/nvm.sh" && nvm install ${NODE_VERSION}
RUN . "$NVM_DIR/nvm.sh" && nvm use v${NODE_VERSION}
RUN . "$NVM_DIR/nvm.sh" && nvm alias default v${NODE_VERSION}
ENV PATH="/root/.nvm/versions/node/v${NODE_VERSION}/bin/:${PATH}"

# Copy files
WORKDIR /app

COPY ./build.gradle /app
COPY ./gradle /app/gradle
COPY ./gradlew /app
COPY ./settings.gradle /app
COPY ./frontend /app/frontend
COPY ./backend /app/backend

# Build
RUN chmod 555 ./gradlew \
#    fix CRLF line endings in gradlew
    && sed -i -e 's/\r$//' ./gradlew \
    && ./gradlew assemble

# -- Create runtime image ---
FROM cm2network/steamcmd AS runtime

ENV APP_VERSION=1.4.0

# TODO try to make the user not root. currently there are problems with mounted volumes ownership
USER root
RUN dpkg --add-architecture i386 \
    && apt-get update \
    && apt-get install -y \
          ca-certificates-java \
          lib32gcc-s1 \
          lib32stdc++6  \
          libcap2 \
          openjdk-17-jre \
          expect \
    && apt-get clean autoclean \
    && apt-get autoremove --yes \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /home/steam
COPY ./config/application-docker.properties /home/steam/config/application.properties
COPY --from=build /app/backend/build/libs/backend-$APP_VERSION.jar /home/steam/app.jar

RUN chown -R root:root /home/steam \
    && chmod -R 755 /home/steam

ENV LANG="en_US.UTF-8"
ENV LANGUAGE="en_US.UTF-8"
ENV LC_ALL="en_US.UTF-8"

ENV STEAMCMD_PATH=/home/steam/steamcmd/steamcmd.sh
ENV DIRECTORY_SERVERS=/home/steam/armaservermanager/servers
ENV DIRECTORY_MODS=/home/steam/armaservermanager/mods
ENV DIRECTORY_LOGS=/home/steam/armaservermanager/logs

EXPOSE 8080/tcp
ENTRYPOINT ["java"]
CMD ["-jar", "./app.jar"]
