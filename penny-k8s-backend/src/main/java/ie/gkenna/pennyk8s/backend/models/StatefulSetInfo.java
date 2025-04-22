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

import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.openapi.models.V1StatefulSetCondition;

import java.util.List;
import java.util.Map;

public class StatefulSetInfo {

	public String name;

	private Map<String, String> annotations;

	private Map<String, String> labels;

	public Integer getAvailableReplicas() {
		return availableReplicas;
	}

	public void setAvailableReplicas(Integer availableReplicas) {
		this.availableReplicas = availableReplicas;
	}

	public Integer getReplicas() {
		return replicas;
	}

	public void setReplicas(Integer replicas) {
		this.replicas = replicas;
	}

	public List<V1StatefulSetCondition> getConditions() {
		return conditions;
	}

	public void setConditions(List<V1StatefulSetCondition> conditions) {
		this.conditions = conditions;
	}

	private Integer availableReplicas;

	private Integer replicas;

	private List<V1StatefulSetCondition> conditions;

	public static StatefulSetInfo fromStatefulSet(V1StatefulSet statefulSet) {
		StatefulSetInfo info = new StatefulSetInfo();
		info.name = statefulSet.getMetadata().getName();
		info.annotations = statefulSet.getMetadata().getAnnotations();
		info.labels = statefulSet.getMetadata().getLabels();

		info.availableReplicas = statefulSet.getStatus().getAvailableReplicas();
		info.replicas = statefulSet.getStatus().getReplicas();
		info.conditions = statefulSet.getStatus().getConditions();

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