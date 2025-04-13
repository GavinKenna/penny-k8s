package ie.gkenna.pennyk8s.models;

import io.kubernetes.client.openapi.models.V1Pod;

public class PodInfo {
    public String name;
    public String namespace;
    public String status;
    public String nodeName;
    public String podIP;
    public String startTime;

    public static PodInfo fromPod(V1Pod pod) {
        PodInfo info = new PodInfo();
        info.name = pod.getMetadata().getName();
        info.namespace = pod.getMetadata().getNamespace();
        info.status = pod.getStatus().getPhase();
        info.nodeName = pod.getSpec().getNodeName();
        info.podIP = pod.getStatus().getPodIP();
        info.startTime = String.valueOf(pod.getStatus().getStartTime());
        return info;
    }
}