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

package ie.gkenna.pennyk8s.models;

import java.util.Map;

import io.kubernetes.client.openapi.models.V1ConfigMap;

public class ConfigMapInfo {

	private String name;

	private String namespace;

	private String creationTimestamp;

	private java.util.Map<String, String> data;

	// Getters and setters...

	public static ConfigMapInfo fromV1ConfigMap(V1ConfigMap cm) {
		ConfigMapInfo info = new ConfigMapInfo();
		info.setName(cm.getMetadata().getName());
		info.setNamespace(cm.getMetadata().getNamespace());
		info.setCreationTimestamp(cm.getMetadata().getCreationTimestamp().toString());
		info.setData(cm.getData());
		return info;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getCreationTimestamp() {
		return this.creationTimestamp;
	}

	public void setCreationTimestamp(String creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public Map<String, String> getData() {
		return this.data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public static ConfigMapInfo fromConfigMap(V1ConfigMap configMap) {
		ConfigMapInfo info = new ConfigMapInfo();
		info.name = configMap.getMetadata().getName();
		info.namespace = configMap.getMetadata().getNamespace();
		info.creationTimestamp = String.valueOf(configMap.getMetadata().getCreationTimestamp());
		info.data = configMap.getData();
		return info;
	}

}
