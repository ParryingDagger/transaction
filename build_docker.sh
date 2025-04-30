#!/usr/local/bin/bash

set -eupo pipefail

IMAGE_NAME=$1
IMAGE_VERSION=$2

mvn clean package

docker build -t ${IMAGE_NAME}:${IMAGE_VERSION} .