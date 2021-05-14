#!/bin/sh
set -e

if [[ -z "${COMMIT_SHA}" ]]; 
then echo "COMMIT_SHA not set"; exit 1
fi
if [[ -z "${SERVER_URL}" ]]; 
then echo "SERVER_URL not set"; exit 1
fi
if [[ -z "${SERVER_IMAGE}" ]]; 
then echo "SERVER_IMAGE not set"; exit 1
fi
if [[ -z "${CLIENT_IMAGE}" ]]; 
then echo "CLIENT_IMAGE not set"; exit 1
fi
if [[ -z "${DOCKER_HOST}" ]]; 
then echo "DOCKER_HOST not set"; exit 1
fi

gcloud components update kubectl
gcloud auth activate-service-account --key-file service-account.json
gcloud auth print-access-token | docker login -u oauth2accesstoken --password-stdin https://${DOCKER_HOST}
gcloud config set project bootscs
gcloud config set compute/zone europe-west3
gcloud container clusters get-credentials bootscs-autopilot-cluster

docker build -t ${DOCKER_HOST}/${SERVER_IMAGE}:latest -t ${DOCKER_HOST}/${SERVER_IMAGE}:${COMMIT_SHA} app/server
docker push ${DOCKER_HOST}/${SERVER_IMAGE}:latest
docker push ${DOCKER_HOST}/${SERVER_IMAGE}:$COMMIT_SHA

docker build -t ${DOCKER_HOST}/${CLIENT_IMAGE}:latest -t ${DOCKER_HOST}/${CLIENT_IMAGE}:${COMMIT_SHA} app/client --build-arg SERVER_URL=${SERVER_URL}
docker push ${DOCKER_HOST}/${CLIENT_IMAGE}:latest
docker push ${DOCKER_HOST}/${CLIENT_IMAGE}:$COMMIT_SHA

kubectl apply -f app/deploy/k8s
kubectl set image deployments/server-deployment server=${DOCKER_HOST}/${SERVER_IMAGE}:$COMMIT_SHA
kubectl set image deployments/client-deployment client=${DOCKER_HOST}/${CLIENT_IMAGE}:$COMMIT_SHA
