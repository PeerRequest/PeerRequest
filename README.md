# PeerRequest

<p align="center">
  <img src="docs/logo.png" width="256" height="256"/>
</p>

<p align="center">~ A peer review platform ~</p>

---

## Development

### Environment Variables

| Key                       | Example                                | Info                                     |
|---------------------------|----------------------------------------|------------------------------------------|
| PR_KEYCLOAK_URL           | https://your.keycloak-instance.example | base URL or your keycloak instance       |
| PR_KEYCLOAK_CLIENT_ID     | peerrequest                            | client id for OAuth2                     |
| PR_KEYCLOAK_CLIENT_SECRET | asd****************dsa                 | client secret for OAuth2                 |
| PR_DATABASE_URL           | jdbc:postgresql://host:port/database   | database url to connect to your database |
| PR_DATABASE_USER          | user                                   | database user                            |
| PR_DATABASE_PASSWORD      | super-secret-password                  | database password                        |

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
- `-p 127.0.0.1:5432:5432` expose database port on your local machine at port `5432` (only accessible on your machine)
- `-p 5432:5432` expose database port on your local machine at port `5432` (accessible in your **local network**)
- `-e POSTGRES_USER=pg` set database user (will also be your database name)
- `-e POSTGRES_PASSWORD=pg` set database password