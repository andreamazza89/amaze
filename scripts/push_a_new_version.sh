#!/bin/bash

set -eu

VERSION_TAG=$1

## Login with ECR
aws ecr get-login-password --region eu-west-1 \
  | docker login --username AWS --password-stdin 556103293623.dkr.ecr.eu-west-1.amazonaws.com

## Build the image and push it up
docker build -t 556103293623.dkr.ecr.eu-west-1.amazonaws.com/amaze:$VERSION_TAG .
docker push 556103293623.dkr.ecr.eu-west-1.amazonaws.com/amaze:$VERSION_TAG
