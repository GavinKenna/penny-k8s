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

package ie.gkenna.pennyk8s.backend.controllers;

import ie.gkenna.pennyk8s.backend.models.*;
import ie.gkenna.pennyk8s.backend.services.PennyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class K8sController {

	private final PennyService pennyService;

	public K8sController(PennyService pennyService) {
		this.pennyService = pennyService;
	}

	@GetMapping("/nodes")
	public List<NodeInfo> getNodes() {
		return this.pennyService.getAllNodes();
	}

	@GetMapping("/pods")
	public List<PodInfo> getPods() {
		return this.pennyService.getAllPods();
	}

	@GetMapping("/configmaps")
	public List<ConfigMapInfo> getConfigMapInfos() {
		return this.pennyService.getAllConfigMaps();
	}

	@GetMapping("/deployments")
	public List<DeploymentInfo> getDeploymentInfos() {
		return this.pennyService.getAllDeployments();
	}

	@GetMapping("/namespaces")
	public List<NamespaceInfo> getNamespaceInfos() {
		return this.pennyService.getAllNamespaces();
	}

	@GetMapping("/pods/{namespace}/{podName}/logs")
	public String getPodLogs(@PathVariable String namespace, @PathVariable String podName) {
		return this.pennyService.getPodLogs(namespace, podName);
	}

}
