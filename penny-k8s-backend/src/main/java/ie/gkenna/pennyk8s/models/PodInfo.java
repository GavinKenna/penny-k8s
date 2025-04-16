package ie.gkenna.pennyk8s.models;

import io.kubernetes.client.openapi.models.V1Pod;

import java.util.List;
import java.util.stream.Collectors;

public class PodInfo {
    public String name;
    public String namespace;
    public String status;
    public String nodeName;
    public String podIP;
    public String startTime;
    public List<ContainerInfo> containers;

    public static PodInfo fromPod(V1Pod pod) {
        PodInfo info = new PodInfo();
        info.name = pod.getMetadata().getName();
        info.namespace = pod.getMetadata().getNamespace();
        info.status = pod.getStatus().getPhase();
        info.nodeName = pod.getSpec().getNodeName();
        info.podIP = pod.getStatus().getPodIP();

        info.containers = pod.getSpec().getContainers().stream()
                .map(ContainerInfo::fromContainer)
                .collect(Collectors.toList());

        info.startTime = String.valueOf(pod.getStatus().getStartTime());
        return info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getPodIP() {
        return podIP;
    }

    public void setPodIP(String podIP) {
        this.podIP = podIP;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public List<ContainerInfo> getContainers() {
        return containers;
    }

    public void setContainers(List<ContainerInfo> containers) {
        this.containers = containers;
    }
}