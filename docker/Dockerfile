FROM openjdk:11-jre
MAINTAINER Jorge Trujillo <jorge.alberto.trujillo@gmail.com>

ENV HOME /root
ENV TIMEZONE=America/Chicago
ENV JAR_NAME webflux-demo.jar

# Prereqs
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl vim lsof jq dnsutils net-tools wget \
    && rm -rf /var/lib/apt/lists/*

# App properties
ENV APP_PATH "/apps/install"
ENV JAR_FILE $APP_PATH/$JAR_NAME

# Copy app (assumes JAR file is placed in the docker folder)
RUN mkdir -p $APP_PATH
COPY $JAR_NAME $APP_PATH/

# App properties and certificates
ENV CERTS_PATH /apps/install/certs
ENV APP_PROPERTIES_PATH /apps/install/app_properties

# Set up entrypoint scripts
RUN mkdir -p /apps/install/core $CERTS_PATH
COPY scripts/*.sh /apps/install/core/
RUN chmod a+x /apps/install/core/*.sh



EXPOSE 8080
ENTRYPOINT ["/apps/install/core/docker-startpoint.sh"]