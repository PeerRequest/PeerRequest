FROM node:slim as js-builder

WORKDIR /project/frontend
COPY frontend/package.json .
RUN npm install
ADD frontend .
RUN mkdir -p ../public && \
    npm run build

FROM gradle:7-jdk17 as java-builder

WORKDIR /project
COPY --chown=gradle:gradle . .
RUN --mount=type=cache,target=/gradle-cache \
    gradle build \
      --no-daemon \
      --build-cache \
      --parallel \
      --project-cache-dir /gradle-cache \
      --exclude-task test

FROM eclipse-temurin:17-jre
RUN useradd -m -d /app peerrequest
WORKDIR /app

COPY --chown=peerrequest spring_split_db_url.sh .
COPY --chown=peerrequest --from=java-builder /project/build/libs/PeerRequest-Backend-*.jar /app/PeerRequest-Backend.jar
COPY --chown=peerrequest --from=js-builder /project/public/ public/

EXPOSE 8080
USER peerrequest
ENTRYPOINT ["/app/spring_split_db_url.sh", "java", "-XX:+UnlockExperimentalVMOptions", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/PeerRequest-Backend.jar"]
