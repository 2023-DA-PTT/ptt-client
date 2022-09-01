FROM ghcr.io/graalvm/graalvm-ce:ol7-java17-22.2.0 AS build

# Install dependencies
RUN gu install native-image

# Build JAR
COPY mvnw /code/mvnw
COPY .mvn /code/.mvn
COPY pom.xml /code/

WORKDIR /code
RUN ./mvnw -B org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline
COPY ./src /code/src
RUN ./mvnw package

# Create image "FROM scratch" :)
FROM alpine
WORKDIR /opt
COPY --from=build /code/target /opt
CMD ["/opt/ptt-client-native"]