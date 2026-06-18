#!/bin/bash

echo "Deploiement du framework ..."

./framework/deploy.sh

sleep 100

echo "Deploiement de l'application"

./app/deploy.sh