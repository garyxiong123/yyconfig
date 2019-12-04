#!/usr/bin/env bash

echo 'ss'

 mvn clean install -Dmaven.test.skip=true

docker build -t garyxiong/apollo-mini:test -f docker/Dockerfile .

docker push garyxiong/apollo-mini:test

docker run -it garyxiong/apollo-mini:test
