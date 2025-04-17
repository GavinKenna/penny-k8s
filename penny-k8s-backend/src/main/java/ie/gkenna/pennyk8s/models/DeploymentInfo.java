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

import java.util.List;
import java.util.stream.Collectors;

import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.openapi.models.V1DeploymentStatus;

public class DeploymentInfo {

	public String name;

	public String namespace;

	public Integer replicas;

	public Integer availableReplicas;

	public Integer updatedReplicas;

	public List<ContainerInfo> containers;

	public static DeploymentInfo fromDeployment(V1Deployment deployment) {
		DeploymentInfo info = new DeploymentInfo();
		info.name = deployment.getMetadata().getName();
		info.namespace = deployment.getMetadata().getNamespace();

		V1DeploymentSpec spec = deployment.getSpec();
		if (spec != null) {
			info.replicas = spec.getReplicas();
			if (spec.getTemplate() != null && spec.getTemplate().getSpec() != null
					&& spec.getTemplate().getSpec().getContainers() != null) {

				info.containers = spec.getTemplate()
					.getSpec()
					.getContainers()
					.stream()
					.map(ContainerInfo::fromContainer)
					.collect(Collectors.toList());
			}
		}

		V1DeploymentStatus status = deployment.getStatus();
		if (status != null) {
			info.availableReplicas = status.getAvailableReplicas();
			info.updatedReplicas = status.getUpdatedReplicas();
		}

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

	public Integer getReplicas() {
		return this.replicas;
	}

	public void setReplicas(Integer replicas) {
		this.replicas = replicas;
	}

	public Integer getAvailableReplicas() {
		return this.availableReplicas;
	}

	public void setAvailableReplicas(Integer availableReplicas) {
		this.availableReplicas = availableReplicas;
	}

	public Integer getUpdatedReplicas() {
		return this.updatedReplicas;
	}

	public void setUpdatedReplicas(Integer updatedReplicas) {
		this.updatedReplicas = updatedReplicas;
	}

	public List<ContainerInfo> getContainers() {
		return this.containers;
	}

	public void setContainers(List<ContainerInfo> containers) {
		this.containers = containers;
	}

}
