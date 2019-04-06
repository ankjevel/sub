ARG GRADLE_VERSION=5.3.1-jdk-alpine
ARG JDK_VERSION=8u191-jdk-alpine

FROM gradle:${GRADLE_VERSION} as builder

ENV GRADLE_USER_HOME=/build
WORKDIR $GRADLE_USER_HOME
USER root

COPY *.gradle ./
COPY src src

RUN gradle --no-daemon installDist

FROM openjdk:${JDK_VERSION}

WORKDIR /app
ENV NAME=Subscriber

COPY --from=builder /build/build/install/*/bin ./bin
COPY --from=builder /build/build/install/*/lib ./lib

COPY src/main/resources/* src/main/resources/

CMD bin/$NAME
