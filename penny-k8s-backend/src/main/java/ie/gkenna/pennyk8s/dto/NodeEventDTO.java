package ie.gkenna.pennyk8s.dto;

import ie.gkenna.pennyk8s.models.NodeInfo;
import ie.gkenna.pennyk8s.models.NodeInfo;
import ie.gkenna.pennyk8s.models.NodeInfo;
import io.kubernetes.client.openapi.models.V1Node;

public class NodeEventDTO {

	private String eventType;




	private NodeInfo node;

	public NodeEventDTO(String eventType, NodeInfo node) {
		this.eventType = eventType;
		this.node = node;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public NodeInfo getNode() {
		return node;
	}

	public void setNode(NodeInfo node) {
		this.node = node;
	}

}
