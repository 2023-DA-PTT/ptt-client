FROM ghcr.io/graalvm/graalvm-ce:ol7-java17-22.2.0 AS build

# Install dependencies
RUN gu install native-image

#WORKDIR /opt

#RUN curl -s http://more.musl.cc/10/x86_64-linux-musl/arm-linux-musleabi-native.tgz --output linux-musl-native.tgz && \
#    curl -s https://zlib.net/zlib-1.2.12.tar.gz --output zlib-1.2.12.tar.gz

#RUN tar zxf linux-musl-native.tgz && \
#    tar -xf zlib-1.2.12.tar.gz && \
#    cd zlib-1.2.12 && \
#    ./configure --prefix=/opt/linux-musl-native --static && \
#    make && \
#    make install

#ENV CC /opt/linux-musl-native/bin/gcc
#ENV PATH="${PATH}:/opt/linux-musl-native/bin"

# Build JAR
COPY mvnw /code/mvnw
COPY .mvn /code/.mvn
COPY pom.xml /code/

WORKDIR /code
RUN ./mvnw -B org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline
COPY ./src /code/src
RUN ./mvnw package -Pnative

# Create image
FROM centos:7
WORKDIR /opt
COPY --from=build /code/target /opt
CMD ["/opt/ptt-client-native"]