#!/bin/sh

: "${BACKEND_HOST:=backend}"
: "${BACKEND_PORT:=8080}"

envsubst '$BACKEND_HOST $BACKEND_PORT' \
    < /etc/nginx/templates/default.conf.template \
    > /etc/nginx/conf.d/default.conf

echo "Nginx configured with BACKEND_HOST=$BACKEND_HOST and BACKEND_PORT=$BACKEND_PORT"

nginx -g 'daemon off;'
