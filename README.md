# PeerRequest

<p align="center">
  <img src="docs/logo.png" width="256" height="256"/>
</p>

<p align="center">~ A peer review platform ~</p>

---

## Deploy

PeerRequests primary deployment method is using [Docker](https://www.docker.com/). This Repository provides a single
docker image
for the backend as well as the frontend.

### Prerequisite

The following items are required to successfully serve a PeerRequest instance.

- [Docker](https://www.docker.com/) to run our container
- [PostgreSQL](https://www.postgresql.org/) database for persistent data
- [Keycloak](https://www.keycloak.org/) instance for authorization
- Email account for sending notifications
- Domain with SSL certificate
- HTTP reverse proxy for SSL

### Guide

This guide uses `https://peerrequest.example.org/` and `https://keycloak.example.org/` in examples.

The following steps describe a rough outline. You find a complete start-to-finish
guide [here](docs/peerrequest-aio-dokku.md).

1. Install **docker** on your host system (See the official [documentation](https://docs.docker.com/get-docker/))
2. Prepare your Keycloak Instance
   1. Install Keycloak (See the official [documentation](https://www.keycloak.org/guides))
      - Keycloak is not required to run on the same machine as PeerRequest
   2. Log in to the admin console
   3. Create a realm
   4. Create a **OpenID Connect** client
   5. Make sure **Standard flow** is **enabled**
   6. Configure your redirect URIs
      - PeerRequests expects callbacks on `https://peerrequest.example.org/login/oauth2/code/keycloak`

   - Create Users in your realm
3. Prepare your database
4. Build the docker image
   ```bash
   docker build -t peerrequest .
   ```
5. Run the container
   - See a list of all possible environment variables and their purpose [here](#environment-variables).
     ```bash
     # you may also set --detach to run the container in the background
     docker run \
      -e PR_KEYCLOAK_URL=https://keycloak.example.org/ \
      -e PR_KEYCLOAK_REALM=PeerRequest \
      -e PR_KEYCLOAK_CLIENT_ID=peerrequest \
      -e PR_KEYCLOAK_CLIENT_SECRET=your_client_secret \
      -e PR_KEYCLOAK_ADMIN_REALM=master \
      -e PR_KEYCLOAK_ADMIN_USER=admin \
      -e PR_KEYCLOAK_ADMIN_CLIENT_ID=admin-cli \
      -e PR_KEYCLOAK_ADMIN_PASSWORD=your_admin_password \
      -e PR_DATABASE_URL=jdbc:postgresql://postgres.exmaple.org:5432/database \
      -e PR_DATABASE_USER=database_user \
      -e PR_DATABASE_PASSWORD=youe_database_password \
      -e PR_SMTP_SERVER=smtp.example.org \
      -e PR_SMTP_PORT=587 \
      -e PR_SMTP_EMAIL=peerrequest@example.org \
      -e PR_SMTP_SERVER_PW=your_smtp_password \
      -e PORT=8080 \
      -p 8080:8080 \
      peerrequest
     ``` 

     You may prefer to pass your [DSN](https://en.wikipedia.org/wiki/Data_source_name) database URL as a single
     environment `$DATABASE_URL`.

     ```bash
     # setting DATABASE_URL will overwrite PR_DATABASE_URL, PR_DATABASE_USER and PR_DATABASE_PASSWORD
     -e DATABASE_URL=postgres://database_user:youe_database_password@postgres.exmaple.org:5432/database
     ```

6. Point your reverse proxy to the container on port `8080`

Now your PeerRequest instance should be up and running.

### Environment Variables

| Key                         | Example                              | Info                                                                                     |
|-----------------------------|--------------------------------------|------------------------------------------------------------------------------------------|
| PR_KEYCLOAK_URL             | https://keycloak.example.org/        | base URL or your keycloak instance                                                       |
| PR_KEYCLOAK_REALM           | PeerRequest                          | name of your keycloak realm                                                              |
| PR_KEYCLOAK_CLIENT_ID       | peerrequest                          | client id for OAuth2                                                                     |
| PR_KEYCLOAK_CLIENT_SECRET   | asd****************dsa               | client secret for OAuth2                                                                 |
| PR_KEYCLOAK_ADMIN_REALM     | master                               | name of your keycloak master realm (Default: `master`)                                   |
| PR_KEYCLOAK_ADMIN_USER      | admin                                | username of the admin account (Default: `admin`)                                         |
| PR_KEYCLOAK_ADMIN_CLIENT_ID | admin-cli                            | client-id of the master realm admin-cli client (Default: `admin-cli`)                    |
| PR_KEYCLOAK_ADMIN_PASSWORD  | ***********                          | password of the admin account                                                            |
| PR_DATABASE_URL             | jdbc:postgresql://host:port/database | database url to connect to your database                                                 |
| PR_DATABASE_USER            | user                                 | database user                                                                            |
| PR_DATABASE_PASSWORD        | super-secret-password                | database password                                                                        |
| PR_LOAD_MOCK                | true                                 | loads mock data for the developers                                                       |
| PORT                        | 8080                                 | defines the port where the backend will listen on inside the container (Default: `8080`) |
| PR_SMTP_SERVER              | smtp.gmail.com                       | smtp server of your email provider                                                       |
| PR_SMTP_PORT                | 587                                  | smtp server port                                                                         |
| PR_SMTP_EMAIL               | example@gmail.com                    | your email address of your email provider                                                |
| PR_SMTP_PW                  | add****************nfs               | smtp server password                                                                     |
| PR_SMTP_USER_NAME           | user_name                            | user name of your email account                                                          |

Convenience script for [DSN](https://en.wikipedia.org/wiki/Data_source_name) database urls

```bash
# set you DSN database url
export DATABASE_URL=postgres://db_user:super-secret-password@database.example.org:5432/my-database

# transform $DATABASE_URL
./spring_split_db_url.sh

# Result
env | grep PR_DATABASE
# > PR_DATABASE_PASSWORD=super-secret-password
# > PR_DATABASE_USER=db_user
# > PR_DATABASE_URL=jdbc:postgresql://database.example.org:5432/my-database
```

### Spin up a temporary development database with docker

```bash
# start container
docker run --name peerrequest-db --rm --tmpfs /var/lib/postgresql/data -p 127.0.0.1:5432:5432 -e POSTGRES_USER=pg -e POSTGRES_PASSWORD=pg postgres

# stop container
docker run peerrequest-db

# remove container (in case you omitted '--rm')
docker rm peerrequest-db
```

- `--name peerrequest-db` Name the container
- `--rm` Remove container when database process returns
- `--tmpfs /var/lib/postgresql/data` store database data directory in RAM (data is **ephemeral**)
- `-p 127.0.0.1:5432:5432` expose database port on your local machine at port `5432` (only accessible from your machine)
- `-p 5432:5432` expose database port on your local machine at port `5432` (accessible from your **local network**)
- `-e POSTGRES_USER=pg` set database user (will also be your database name)
- `-e POSTGRES_PASSWORD=pg` set database password
