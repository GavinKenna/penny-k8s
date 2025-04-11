package ie.gkenna.pennyk8s.models;

import io.kubernetes.client.openapi.models.V1Pod;

public class PodInfo {
    public String name;
    public String namespace;
    public String status;

    public static PodInfo fromPod(V1Pod pod) {
        PodInfo info = new PodInfo();
        info.name = pod.getMetadata().getName();
        info.namespace = pod.getMetadata().getNamespace();
        info.status = pod.getStatus().getPhase();
        return info;
    }
}