FROM gradle:7-jdk17 as builder

WORKDIR /project
COPY --chown=gradle:gradle . .
RUN --mount=type=cache,target=/gradle-cache \
    gradle build --no-daemon --build-cache --parallel --project-cache-dir /gradle-cache

FROM eclipse-temurin:17-jre
RUN useradd -m -d /app peerrequest
WORKDIR /app

COPY --chown=peerrequest spring_split_db_url.sh .
COPY --chown=peerrequest --from=builder /project/build/libs/PeerRequest-Backend-*.jar /app/PeerRequest-Backend.jar

EXPOSE 8080
USER peerrequest
ENTRYPOINT ["/app/spring_split_db_url.sh", "java", "-XX:+UnlockExperimentalVMOptions", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/PeerRequest-Backend.jar"]
