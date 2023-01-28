#!/bin/bash

# `cut` is used to cut out the separators (:, @, /) that come matched with the groups.

if [[ -n $DATABASE_URL ]]
then
  DATABASE_USER=$(echo "$DATABASE_URL" | grep -oP "postgres://\K(.+?):" | cut -d: -f1)
  DATABASE_PASSWORD=$(echo "$DATABASE_URL" | grep -oP "postgres://.*:\K(.+?)@" | cut -d@ -f1)
  DATABASE_HOST=$(echo "$DATABASE_URL" | grep -oP "postgres://.*@\K(.+?):" | cut -d: -f1)
  DATABASE_PORT=$(echo "$DATABASE_URL" | grep -oP "postgres://.*@.*:\K(\d+)/" | cut -d/ -f1)
  DATABASE_NAME=$(echo "$DATABASE_URL" | grep -oP "postgres://.*@.*:.*/\K(.+?)$")

  export PR_DATABASE_URL="jdbc:postgresql://$DATABASE_HOST:$DATABASE_PORT/$DATABASE_NAME"
  export PR_DATABASE_USER=$DATABASE_USER
  export PR_DATABASE_PASSWORD=$DATABASE_PASSWORD
fi

$@
