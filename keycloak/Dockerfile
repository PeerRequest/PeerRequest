FROM quay.io/keycloak/keycloak:latest as builder

# health and metrics support
ENV KC_HEALTH_ENABLED=false
ENV KC_METRICS_ENABLED=false

# enable http listener to run behind reverse proxy
ENV KC_HTTP_ENABLED=true
ENV KC_PROXY=edge
ENV KC_HTTP_PORT=8080

# logging
ENV KC_LOG=console
ENV KC_LOG_CONSOLE_COLOR=true

# Configure a database vendor
ENV KC_DB=postgres

WORKDIR /opt/keycloak
# We create a self-signed certificate here because without keycloak refuses to start.
# We do not use it afterwards because our reverse proxy takes care of TLS.
RUN keytool -genkeypair -storepass password -storetype PKCS12 -keyalg RSA -keysize 2048 -dname "CN=keycloak" -alias keycloak -ext "SAN:c=DNS:keycloak,IP:127.0.0.1" -keystore conf/server.keystore
RUN /opt/keycloak/bin/kc.sh build

USER root
COPY split_db_url.sh /split_db_url.sh
RUN chmod +x /split_db_url.sh

FROM quay.io/keycloak/keycloak:latest
COPY --from=builder /opt/keycloak/ /opt/keycloak/
COPY --from=builder /split_db_url.sh /split_db_url.sh

# The following values should be set by the host
# ENV DATABASE_URL=
# ENV KC_HOSTNAME=
# ENV KEYCLOAK_ADMIN=
# ENV KEYCLOAK_ADMIN_PASSWORD=

# health and metrics support
ENV KC_HEALTH_ENABLED=false
ENV KC_METRICS_ENABLED=false

# enable http listener to run behind reverse proxy
ENV KC_HTTP_ENABLED=true
ENV KC_PROXY=edge
ENV KC_HTTP_PORT=8080

# logging
ENV KC_LOG=console
ENV KC_LOG_CONSOLE_COLOR=true

EXPOSE 8080
ENTRYPOINT ["/split_db_url.sh", "/opt/keycloak/bin/kc.sh", "start", "--optimized"]
