FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

WORKDIR /wakamiti

ENV TZ Europe/Madrid
ENV WAKAMITI_HOME /opt/wakamiti
ENV WAKAMITI_REPOSITORY /root/.local/share/wakamiti/repository

RUN apk add --no-cache bash

COPY src/entrypoint.sh /

# launcher executables are including in the staging directory (from wakamiti-launcher)

COPY target/assembly/staging $WAKAMITI_HOME/

RUN mkdir -p $WAKAMITI_REPOSITORY \
    && chmod +x /entrypoint.sh \
    && chmod +x $WAKAMITI_HOME/wakamiti \
    && echo $WAKAMITI_HOME >> ~/.profile \
    && ln -s $WAKAMITI_HOME/wakamiti /usr/local/bin/wakamiti \
    && chmod +x /usr/local/bin/wakamiti



ENTRYPOINT ["/entrypoint.sh"]
