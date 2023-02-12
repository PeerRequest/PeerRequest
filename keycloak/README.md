# [Keycloak](https://www.keycloak.org/)

PeerRequest uses [Keycloak](https://www.keycloak.org/) as authentication server.

## Build Keycloak container image

```bash
docker build -t peerrequest-keycloak .
```

## Run Keycloak instance

Our Keycloak instance requires:

- a [PostgreSQL](https://www.postgresql.org/) database
- a reverse proxy to handle TLS
- the following environment variables

### Container environment variables

| ENV                     | Description                                                                     |
|-------------------------|---------------------------------------------------------------------------------|
| DATABASE_URL            | Database DSN (example: "postgres://user:password@postgresdb.net:5432/keycloak") |
| KC_HOSTNAME             | Hostname to reach Keycloak                                                      |
| KEYCLOAK_ADMIN          | Keycloak Admin Username                                                         |
| KEYCLOAK_ADMIN_PASSWORD | Keycloak Admin Password                                                         |

Find all environment variables [here](https://www.keycloak.org/server/all-config).