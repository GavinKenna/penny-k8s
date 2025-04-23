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

import io.kubernetes.client.openapi.models.V1ClusterRoleBinding;
import io.kubernetes.client.openapi.models.V1PolicyRule;
import io.kubernetes.client.openapi.models.V1Role;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClusterRoleBindingInfo {

	public String name;

	private Map<String, String> annotations;

	private Map<String, String> labels;

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleKind() {
		return roleKind;
	}

	public void setRoleKind(String roleKind) {
		this.roleKind = roleKind;
	}

	public List<String> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}

	private String roleName;
	private String roleKind; // "ClusterRole" or "Role"
	private List<String> subjects; // usernames, service accounts, etc.

	public static ClusterRoleBindingInfo fromRole(V1ClusterRoleBinding clusterRoleBinding) {
		ClusterRoleBindingInfo info = new ClusterRoleBindingInfo();
		info.name = clusterRoleBinding.getMetadata().getName();
		info.annotations = clusterRoleBinding.getMetadata().getAnnotations();
		info.labels = clusterRoleBinding.getMetadata().getLabels();

		info.roleName = clusterRoleBinding.getRoleRef().getName();
		info.roleKind = clusterRoleBinding.getRoleRef().getKind();
		info.subjects = clusterRoleBinding.getSubjects().stream()
				.map(subject -> String.format("%s:%s%s",
						subject.getKind(),
						subject.getName(),
						subject.getNamespace() != null && !subject.getNamespace().isEmpty()
								? " (ns: " + subject.getNamespace() + ")"
								: ""
				))
				.collect(Collectors.toList());
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