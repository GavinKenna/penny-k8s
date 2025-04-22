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

import io.kubernetes.client.openapi.models.V1ClusterRole;
import io.kubernetes.client.openapi.models.V1PolicyRule;

import java.util.List;
import java.util.Map;

public class ClusterRoleInfo {

	public String name;

	public String startTime;

	// TODO perhaps have an abstract class that holds annotations, names, labels, as every
	// model has this
	private Map<String, String> annotations;

	private Map<String, String> labels;

	public List<V1PolicyRule> getRules() {
		return rules;
	}

	public void setRules(List<V1PolicyRule> rules) {
		this.rules = rules;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	private List<V1PolicyRule> rules;

	public static ClusterRoleInfo fromClusterRole(V1ClusterRole clusterRole) {
		ClusterRoleInfo info = new ClusterRoleInfo();
		info.name = clusterRole.getMetadata().getName();
		info.annotations = clusterRole.getMetadata().getAnnotations();
		info.labels = clusterRole.getMetadata().getLabels();
		info.startTime = String.valueOf(clusterRole.getMetadata().getCreationTimestamp());
		info.rules = clusterRole.getRules();

		return info;
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

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}