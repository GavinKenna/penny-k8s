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

import io.kubernetes.client.openapi.models.V1ServiceAccount;
import io.kubernetes.client.openapi.models.V1ObjectMeta;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceAccountInfo {

	public String name;

	private String namespace;

	private Map<String, String> annotations;

	private Map<String, String> labels;

	private OffsetDateTime creationTimestamp;

	private List<String> secrets;

	private List<String> imagePullSecrets;

	public static ServiceAccountInfo fromServiceAccount(V1ServiceAccount sa) {
		ServiceAccountInfo info = new ServiceAccountInfo();
		V1ObjectMeta metadata = sa.getMetadata();

		info.name = metadata.getName();
		info.namespace = metadata.getNamespace();
		info.annotations = metadata.getAnnotations();
		info.labels = metadata.getLabels();
		info.creationTimestamp = metadata.getCreationTimestamp();

		if (sa.getSecrets() != null) {
			info.secrets = sa.getSecrets().stream()
					.map(ref -> ref.getName())
					.collect(Collectors.toList());
		}

		if (sa.getImagePullSecrets() != null) {
			info.imagePullSecrets = sa.getImagePullSecrets().stream()
					.map(ref -> ref.getName())
					.collect(Collectors.toList());
		}

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

	public OffsetDateTime getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(OffsetDateTime creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public List<String> getSecrets() {
		return secrets;
	}

	public void setSecrets(List<String> secrets) {
		this.secrets = secrets;
	}

	public List<String> getImagePullSecrets() {
		return imagePullSecrets;
	}

	public void setImagePullSecrets(List<String> imagePullSecrets) {
		this.imagePullSecrets = imagePullSecrets;
	}
}
