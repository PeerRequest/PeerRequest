# Hosting PeerRequest from start to finish using [Dokku](https://dokku.com/)

This guide will go through all the steps required to set up PeerRequest and all dependencies using dokku on a linux
system including SSL configuration with [Let's Encrypt](https://letsencrypt.org/).

This guide assumes you can **SSH** into your server and you have **sudo** privileges.

1. Make sure you can reach your host system on your desired domain.
2. Follow the [official guide](https://dokku.com/docs/getting-started/installation/) to install **dokku** on your host
   system.
   - Make sure you can log in to your server with the **dokku** user via SSH
3. Install the [**dokku-letsencrypt**](https://github.com/dokku/dokku-letsencrypt#installation) plugin
4. Install the [**dokku-postgres**](https://github.com/dokku/dokku-postgres#installation) plugin
5. Build the Keycloak docker image ([Details](../keycloak))
   - On your local machine inside the cloned repository:

   ```bash
   cd keycloak
   docker build -t peerrequest-keycloak .
   
   # send the image to your server
   docker save peerrequest-keycloak | ssh myvps "sudo docker load"
   ```

6. Create a new Keycloak instance in dokku
   - On the host system:

     ```bash
     # create a new Keycloak instance
     dokku apps:create peerrequest-keycloak
     
     # set a domain for keycloak
     dokku domains:set peerrequest-keycloak keycloak.example.org
     
     # specify an email address for Let's Encrypt notifications
     dokku config:set --no-restart peerrequest-keycloak DOKKU_LETSENCRYPT_EMAIL=report@example.org
     
     # specify port mappings for keycloak
     dokku proxy:ports-set peerrequest-keycloak http:80:8080 https:443:8080
     
     # create and link a database for keycloak
     dokku postgres:create peerrequest-keycloak-postgres
     dokku postgres:link peerrequest-keycloak-postgres peerrequest-keycloak
     
     # configure keycloak
     dokku config:set --no-restart peerrequest-keycloak \
        KC_HOSTNAME="keycloak.example.org" \
        KEYCLOAK_ADMIN="admin" \
        KEYCLOAK_ADMIN_PASSWORD="your_keycloak_admin_password"
     dokku nginx:set peerrequest-keycloak proxy_set_header "X-Forwarded-Host $http_host"
     dokku nginx:set peerrequest-keycloak client-max-body-size 64m
     
     # tag the keycloak image
     sudo docker tag peerrequest-keycloak dokku/peerrequest-keycloak:latest
     
     # start keycloak
     dokku ps:restart peerrequest-keycloak
     
     # enable Let's Encrypt
     dokku letsencrypt:enable peerrequest-keycloak
     
     # you can check the logs anytime with
     dokku logs peerrequest-keycloak
     ```

7. Configure Keycloak like in the [general guide](../README.md#guide)
8. Create a new PeerRequest instance in dokku
   - On the host system:

      ```bash
      # create a new app
      dokku apps:create peerrequest
   
      # set build mode
      dokku builder:set peerrequest selected dockerfile
   
      # set a domain
      dokku domains:set peerrequest peerrequest.example.org
   
      # specify an email address for Let's Encrypt notifications
      dokku config:set --no-restart peerrequest DOKKU_LETSENCRYPT_EMAIL=report@example.org
   
      # specify port mappings
      dokku proxy:ports-set peerrequest http:80:8080 https:443:8080
   
      # create and link a database
      dokku postgres:create peerrequest-postgres
      dokku postgres:link peerrequest-postgres peerrequest
   
      # configure PeerRequest
      dokku config:set --no-restart peerrequest \
       PR_KEYCLOAK_URL=https://keycloak.example.org/ \
       PR_KEYCLOAK_REALM=PeerRequest \
       PR_KEYCLOAK_CLIENT_ID=peerrequest \
       PR_KEYCLOAK_CLIENT_SECRET=your_client_id \
       PR_KEYCLOAK_ADMIN_REALM=master \
       PR_KEYCLOAK_ADMIN_USER=admin \
       PR_KEYCLOAK_ADMIN_CLIENT_ID=admin-cli \
       PR_KEYCLOAK_ADMIN_PASSWORD=your_keycloak_admin_password \
       PR_SMTP_SERVER=smtp.example.org \
       PR_SMTP_PORT=587 \
       PR_SMTP_EMAIL=peerrequest@example.org \
       PR_SMTP_APP_PW=your_smtp_password
      dokku nginx:set peerrequest client-max-body-size 64m
   
      # enable Let's Encrypt
      dokku letsencrypt:enable peerrequest
   
      # you can check the logs anytime with
      dokku logs peerrequest
      ```
     Dokku is now ready to build and serve PeerRequest.


- On your local machine inside the cloned repository:

   ```bash
   # this will push the repository to dokku
   # dokku will automatically build the project and run it
   git push ssh://dokku@your_server:peerrequest
   ```

PeerRequest should now be available at `https://peerrequest.example.org`
