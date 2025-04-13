cd backend

bash docker-build-and-push.sh

cd ../penny-k8s-frontend

bash docker-build-and-push.sh

cd ../

helm delete penny-k8s


helm package helm-chart/

helm install penny-k8s penny-k8s-0.1.0.tgz