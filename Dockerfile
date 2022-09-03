FROM ghcr.io/graalvm/graalvm-ce:ol7-java17-22.2.0 AS build

# Install dependencies
RUN gu install native-image

WORKDIR /opt

RUN curl http://more.musl.cc/10/x86_64-linux-musl/x86_64-linux-musl-native.tgz --output x86_64-linux-musl-native.tgz && \
    curl https://zlib.net/zlib-1.2.12.tar.gz --output zlib-1.2.12.tar.gz

RUN tar zxf x86_64-linux-musl-native.tgz && \
    tar -xf zlib-1.2.12.tar.gz && \
    cd zlib-1.2.12 && \
    ./configure && \
    make && \
    make install

ENV CC /opt/x86_64-linux-musl-native/bin/gcc
ENV PATH="${PATH}:/opt/x86_64-linux-musl-native/bin"

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
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build \
    -ldflags="-w -s" -o $PROJ_BIN_PATH ./cmd/...
CMD ["/code/target/ptt-client-native"]