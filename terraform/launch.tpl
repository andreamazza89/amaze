#!/bin/sh

set -eu

curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

sudo amazon-linux-extras install docker -y
sudo service docker start

sudo /usr/local/bin/aws ecr get-login-password --region eu-west-1 | sudo docker login --username AWS --password-stdin 556103293623.dkr.ecr.eu-west-1.amazonaws.com
sudo docker run -p 80:8080 556103293623.dkr.ecr.eu-west-1.amazonaws.com/amaze:${version_tag}

