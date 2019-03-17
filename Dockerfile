ARG VERSION=8u191-jdk-alpine

FROM openjdk:${VERSION} as builder

WORKDIR /build

COPY gradle gradle
COPY gradlew* ./
COPY *.gradle ./

RUN ./gradlew --no-daemon classes

COPY src src

RUN ./gradlew --no-daemon installDist

FROM openjdk:${VERSION}

WORKDIR /app

COPY --from=builder /build/build/install/*/bin ./bin
COPY --from=builder /build/build/install/*/lib ./lib

COPY src/main/resources/* src/main/resources/

CMD bin/Subscriber
