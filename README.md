# E-CommerceCloudMigration
E-Commerce Site/API Bug Fixes, New Features, and Cloud Migration with Minikube

# RUN
minikube start --driver=kvm2 && \
minikube addons enable registry && \
eval $(minikube -p minikube docker-env) && \
docker build -t easy-api-service:latest ./EasyAPI && \
docker build -t easy-spa-service:latest ./FrontEnd && \
helm install easy-deployment ./Helm/easy-core

# Port Forward Envoy
kubectl port-forward svc/envoy 8080:80