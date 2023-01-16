# [KeyCloak](https://www.keycloak.org/)

PeerRequest uses [KeyCloak](https://www.keycloak.org/) as authentication server.

## Build KeyCloak container image

```bash
docker build -t peerrequest-keycloak .
```

## Run KeyCloak instance

Our KeyCloak instance requires:

- a [PostgreSQL](https://www.postgresql.org/) database
- a reverse proxy to handle TLS
- the following environment variables

### Container environment variables

| ENV                     | Description                                                                     |
|-------------------------|---------------------------------------------------------------------------------|
| DATABASE_URL            | Database DSN (example: "postgres://user:password@postgresdb.net:5432/keycloak") |
| KC_HOSTNAME             | Hostname to reach KeyCloak                                                      |
| KEYCLOAK_ADMIN          | KeyCloak Admin Username                                                         |
| KEYCLOAK_ADMIN_PASSWORD | KeyCloak Admin Password                                                         |

Find all environment variables [here](https://www.keycloak.org/server/all-config).