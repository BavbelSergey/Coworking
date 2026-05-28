#!/bin/sh

: "${BACKEND_HOST:=backend}"
: "${BACKEND_PORT:=8080}"

echo "=== ENTRYPOINT START ==="
echo "BACKEND_HOST=$BACKEND_HOST"
echo "BACKEND_PORT=$BACKEND_PORT"

envsubst '$BACKEND_HOST $BACKEND_PORT' \
    < /etc/nginx/templates/default.conf.template \
    > /etc/nginx/conf.d/default.conf

echo "=== GENERATED NGINX CONFIG ==="
cat /etc/nginx/conf.d/default.conf
echo "=== END CONFIG ==="

nginx -g 'daemon off;'
