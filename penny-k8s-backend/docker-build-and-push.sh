docker build -t penny-k8s-backend .

docker tag penny-k8s-backend 192.168.88.23:5000/penny-k8s-backend:latest

docker push 192.168.88.23:5000/penny-k8s-backend
