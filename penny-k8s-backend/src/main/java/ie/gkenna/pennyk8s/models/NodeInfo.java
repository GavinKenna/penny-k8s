package ie.gkenna.pennyk8s.models;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeCondition;

import java.util.Map;

public class NodeInfo {

	public String name;

	public String status;

	public String cpu;

	public String memory;

	public static NodeInfo fromNode(V1Node node) {
		NodeInfo info = new NodeInfo();
		info.name = node.getMetadata().getName();
		info.status = info.getNodeStatus(node);

		Map<String, Quantity> capacity = node.getStatus().getCapacity();
		if (capacity != null) {
			info.cpu = capacity.get("cpu").getNumber().toString();
			info.memory = capacity.get("memory").getNumber().toString(); // Usually in Ki
		}
		return info;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMemory() {
		return memory;
	}

	public void setMemory(String memory) {
		this.memory = memory;
	}

	public String getCpu() {
		return cpu;
	}

	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNodeStatus(V1Node node) {
		for (V1NodeCondition condition : node.getStatus().getConditions()) {
			if ("Ready".equals(condition.getType())) {
				return "Ready".equals(condition.getType()) && "True".equals(condition.getStatus()) ? "Ready"
						: "Not Ready";
			}
		}
		return "Unknown";
	}

}