#!/bin/sh +x

NAME=${HOSTNAME%%.*}

echo "Starting Locator ${NAME} on port 10334"

gfsh start locator --name=${NAME} --mcast-port=0

while true; do
  sleep 10
done