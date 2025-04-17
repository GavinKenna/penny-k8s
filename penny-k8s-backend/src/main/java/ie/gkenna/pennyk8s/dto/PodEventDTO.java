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
		return this.eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public PodInfo getPod() {
		return this.pod;
	}

	public void setPod(PodInfo pod) {
		this.pod = pod;
	}

}
