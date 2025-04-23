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

import io.kubernetes.client.openapi.models.V1ReplicaSet;
import io.kubernetes.client.openapi.models.V1ObjectMeta;

import java.time.OffsetDateTime;
import java.util.Map;

public class ReplicaSetInfo {

	public String name;

	private String namespace;

	private Map<String, String> annotations;

	private Map<String, String> labels;

	private Integer replicas;

	private Integer availableReplicas;

	private Integer readyReplicas;

	private OffsetDateTime creationTimestamp;

	public static ReplicaSetInfo fromReplicaSet(V1ReplicaSet rs) {
		ReplicaSetInfo info = new ReplicaSetInfo();
		V1ObjectMeta metadata = rs.getMetadata();

		info.name = metadata.getName();
		info.namespace = metadata.getNamespace();
		info.annotations = metadata.getAnnotations();
		info.labels = metadata.getLabels();
		info.creationTimestamp = metadata.getCreationTimestamp();

		info.replicas = rs.getSpec() != null ? rs.getSpec().getReplicas() : null;
		info.availableReplicas = rs.getStatus() != null ? rs.getStatus().getAvailableReplicas() : null;
		info.readyReplicas = rs.getStatus() != null ? rs.getStatus().getReadyReplicas() : null;

		return info;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public Map<String, String> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Map<String, String> annotations) {
		this.annotations = annotations;
	}

	public Map<String, String> getLabels() {
		return labels;
	}

	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}

	public Integer getReplicas() {
		return replicas;
	}

	public void setReplicas(Integer replicas) {
		this.replicas = replicas;
	}

	public Integer getAvailableReplicas() {
		return availableReplicas;
	}

	public void setAvailableReplicas(Integer availableReplicas) {
		this.availableReplicas = availableReplicas;
	}

	public Integer getReadyReplicas() {
		return readyReplicas;
	}

	public void setReadyReplicas(Integer readyReplicas) {
		this.readyReplicas = readyReplicas;
	}

	public OffsetDateTime getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(OffsetDateTime creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}
}
