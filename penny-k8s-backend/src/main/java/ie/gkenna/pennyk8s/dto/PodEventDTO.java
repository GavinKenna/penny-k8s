package ie.gkenna.pennyk8s.dto;

import ie.gkenna.pennyk8s.models.PodInfo;

public class PodEventDTO {

	private String eventType;

	private PodInfo pod;

	public PodEventDTO(String eventType, PodInfo pod) {
		this.eventType = eventType;
		this.pod = pod;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public PodInfo getPod() {
		return pod;
	}

	public void setPod(PodInfo pod) {
		this.pod = pod;
	}

}
