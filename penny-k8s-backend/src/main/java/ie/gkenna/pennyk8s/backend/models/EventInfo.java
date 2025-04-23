/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ie.gkenna.pennyk8s.backend.models;

import io.kubernetes.client.openapi.models.CoreV1Event;
import io.kubernetes.client.openapi.models.V1ObjectReference;
import io.kubernetes.client.openapi.models.V1PolicyRule;
import io.kubernetes.client.openapi.models.V1Role;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class EventInfo {
	private String name;
	private String namespace;
	private String type;         // Normal / Warning
	private String reason;       // e.g., FailedScheduling
	private String message;      // human-readable
	private String involvedObjectKind;
	private String involvedObjectName;
	private String involvedObjectNamespace;
	private OffsetDateTime eventTime;

	public EventInfo fromEvent(CoreV1Event event) {
		EventInfo info = new EventInfo();
		info.name = event.getMetadata().getName();
		info.namespace = event.getMetadata().getNamespace();
		info.type = event.getType();
		info.reason = event.getReason();
		info.message = event.getMessage();

		V1ObjectReference involved = event.getInvolvedObject();
		if (involved != null) {
			this.involvedObjectKind = involved.getKind();
			this.involvedObjectName = involved.getName();
			this.involvedObjectNamespace = involved.getNamespace();
		}

		this.eventTime = event.getLastTimestamp(); // or getEventTime() in newer APIs

		return info;
	}

	// Getters (add setters if you want to make it mutable)
	public String getName() { return name; }
	public String getNamespace() { return namespace; }
	public String getType() { return type; }
	public String getReason() { return reason; }
	public String getMessage() { return message; }
	public String getInvolvedObjectKind() { return involvedObjectKind; }
	public String getInvolvedObjectName() { return involvedObjectName; }
	public String getInvolvedObjectNamespace() { return involvedObjectNamespace; }
	public OffsetDateTime getEventTime() { return eventTime; }
}