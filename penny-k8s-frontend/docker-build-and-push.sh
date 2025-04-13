docker build -t penny-k8s-frontend .

docker tag penny-k8s-frontend 192.168.88.23:5000/penny-k8s-frontend:latest

docker push 192.168.88.23:5000/penny-k8s-frontend
