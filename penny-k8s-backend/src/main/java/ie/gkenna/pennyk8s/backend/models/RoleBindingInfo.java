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

import io.kubernetes.client.openapi.models.RbacV1Subject;
import io.kubernetes.client.openapi.models.V1RoleBinding;

import java.util.List;
import java.util.Map;

public class RoleBindingInfo {

	public String name;

	private Map<String, String> annotations;

	private Map<String, String> labels;

	public List<RbacV1Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<RbacV1Subject> subjects) {
		this.subjects = subjects;
	}

	private List<RbacV1Subject> subjects;

	public static RoleBindingInfo fromRoleBinding(V1RoleBinding roleBinding) {
		RoleBindingInfo info = new RoleBindingInfo();
		info.name = roleBinding.getMetadata().getName();
		info.annotations = roleBinding.getMetadata().getAnnotations();
		info.labels = roleBinding.getMetadata().getLabels();

		info.subjects = roleBinding.getSubjects();

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