# --- Build ---
FROM openjdk:17-jdk-slim-buster AS build

#Install NodeJS + NPM
RUN apt-get update \
	  && apt-get install -y \
          curl \
          nodejs \
          npm \
    && apt-get clean autoclean \
    && apt-get autoremove --yes \
    && rm -rf /var/lib/apt/lists/*

RUN npm install n -g \
    && npm install npm@latest -g \
    && n stable

# Copy files
WORKDIR /app

COPY ./build.gradle /app
COPY ./gradle /app/gradle
COPY ./gradlew /app
COPY ./settings.gradle /app
COPY ./src /app/src

# Build
RUN chmod 555 ./gradlew \
    && ./gradlew assemble

# -- Create runtime image ---
FROM cm2network/steamcmd AS runtime

ENV APP_VERSION=1.0.0

# TODO try to make the user not root. currently there are problems with mounted volumes ownership
USER root
RUN dpkg --add-architecture i386 \
    && apt-get update \
    && apt-get install -y \
          lib32gcc-s1 \
          lib32stdc++6  \
          libcap2 \
          openjdk-17-jre \
    && apt-get clean autoclean \
    && apt-get autoremove --yes \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /home/steam
COPY ./config/application-docker.properties /home/steam/config/application.properties
COPY --from=build /app/build/libs/arma3-server-gui-$APP_VERSION.jar /home/steam/app.jar

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