#!/bin/sh +x
LOCATORS="locator1_1[10334]"

NAME=${HOSTNAME%%.*}

if [ ! -d /data/$NAME ]; then
  mkdir -p /data/$NAME
fi

cd /data/

echo "Sleeping for 60s because Geode..."
sleep 60

echo "Starting Server ${NAME} with locators ${LOCATORS}"

gfsh start server \
  --name=${NAME} \
  --locators=${LOCATORS} \
  --dir=/data/${NAME}
