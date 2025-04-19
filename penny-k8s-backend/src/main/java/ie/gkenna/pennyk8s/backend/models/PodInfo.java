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

import java.util.List;
import java.util.stream.Collectors;

import io.kubernetes.client.openapi.models.V1Pod;

public class PodInfo {

	public String name;

	public String namespace;

	public String status;

	public String nodeName;

	public String podIP;

	public String startTime;

	public List<ContainerInfo> containers;

	public static PodInfo fromPod(V1Pod pod) {
		PodInfo info = new PodInfo();
		info.name = pod.getMetadata().getName();
		info.namespace = pod.getMetadata().getNamespace();
		info.status = pod.getStatus().getPhase();
		info.nodeName = pod.getSpec().getNodeName();
		info.podIP = pod.getStatus().getPodIP();

		info.containers = pod.getSpec()
			.getContainers()
			.stream()
			.map(ContainerInfo::fromContainer)
			.collect(Collectors.toList());

		info.startTime = String.valueOf(pod.getStatus().getStartTime());
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

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNodeName() {
		return this.nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getPodIP() {
		return this.podIP;
	}

	public void setPodIP(String podIP) {
		this.podIP = podIP;
	}

	public String getStartTime() {
		return this.startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public List<ContainerInfo> getContainers() {
		return this.containers;
	}

	public void setContainers(List<ContainerInfo> containers) {
		this.containers = containers;
	}

}
