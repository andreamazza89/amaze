#!/bin/bash

set -eu

VERSION_TAG=$1

echo "Don't forget to actually build a new version of the app (build the frontend and the jar)"

echo "Loggin into ECR"
aws ecr get-login-password --region eu-west-1 \
  | docker login --username AWS --password-stdin 556103293623.dkr.ecr.eu-west-1.amazonaws.com

echo "Building the image and then pushing it up"
docker build -t 556103293623.dkr.ecr.eu-west-1.amazonaws.com/amaze:$VERSION_TAG .
docker push 556103293623.dkr.ecr.eu-west-1.amazonaws.com/amaze:$VERSION_TAG
