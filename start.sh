#!/bin/bash

if [ -z "$1" ]; then
  echo "Usage: ./start.sh <PORT>"
  exit 1
fi

export APP_PORT=$1

echo "Starting Stock Market API on localhost:${APP_PORT}"

docker compose up -d --build --scale stockmarket-core=5

echo "Waiting for Application instances to be fully ready..."

while true; do
  HTTP_STATUS=$(curl -s -L -o /dev/null -w "%{http_code}" "http://localhost:${APP_PORT}/swagger-ui.html" || echo "000")

  if [ "$HTTP_STATUS" == "200" ]; then
    echo ""
    break
  else
    echo -n "."
    sleep 3 
  fi
done

echo ""
echo "Application is READY and accepting traffic!"
echo "Swagger UI: http://localhost:${APP_PORT}/swagger-ui.html"