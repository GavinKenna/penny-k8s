package ie.gkenna.pennyk8s.models;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1Node;

import java.util.Map;

public class NodeInfo {
    public String name;
    public String status;
    public String cpu;
    public String memory;

    public static NodeInfo fromNode(V1Node node) {
        NodeInfo info = new NodeInfo();
        info.name = node.getMetadata().getName();
        info.status = node.getStatus().getConditions().stream()
                .filter(c -> "Ready".equals(c.getType()))
                .findFirst()
                .map(c -> c.getStatus())
                .orElse("Unknown");

        Map<String, Quantity> capacity = node.getStatus().getCapacity();
        if (capacity != null) {
            info.cpu = capacity.get("cpu").getNumber().toString();
            info.memory = capacity.get("memory").getNumber().toString(); // Usually in Ki
        }
        return info;
    }
}