package ie.gkenna.pennyk8s.models;

import io.kubernetes.client.openapi.models.V1Node;

public class NodeInfo {
    public String name;
    public String status;

    public static NodeInfo fromNode(V1Node node) {
        NodeInfo info = new NodeInfo();
        info.name = node.getMetadata().getName();
        info.status = node.getStatus().getConditions().stream()
                .filter(c -> "Ready".equals(c.getType()))
                .findFirst()
                .map(c -> c.getStatus())
                .orElse("Unknown");
        return info;
    }
}