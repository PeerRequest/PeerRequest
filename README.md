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