FROM ubuntu:20.04
MAINTAINER David Salathe <salathe.david@gmail.com>

RUN apt update -y && apt install -y openjdk-11-jre-headless && rm -rf /var/lib/apt/lists/*

ENV APP_HOME /app

RUN groupadd -r app && useradd -r -gapp app
RUN mkdir -m 0755 -p ${APP_HOME}/bin
RUN mkdir -m 0755 -p ${APP_HOME}/config
RUN mkdir -m 0755 -p ${APP_HOME}/logs/

COPY target/tchutchu.jar ${APP_HOME}/bin
COPY docker-entrypoint.sh /

RUN chown -R app:app ${APP_HOME}
RUN chmod +x /docker-entrypoint.sh

EXPOSE 8080
EXPOSE 8443

WORKDIR ${APP_HOME}
CMD ["/docker-entrypoint.sh"]

