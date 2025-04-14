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
}