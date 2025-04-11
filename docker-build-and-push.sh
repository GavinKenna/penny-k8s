docker build -t penny-k8s .

docker tag penny-k8s 192.168.88.23:5000/penny-k8s:latest

docker push 192.168.88.23:5000/penny-k8s
