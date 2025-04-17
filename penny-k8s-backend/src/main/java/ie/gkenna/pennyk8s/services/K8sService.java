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

package ie.gkenna.pennyk8s.services;

import com.google.gson.reflect.TypeToken;
import ie.gkenna.pennyk8s.models.ConfigMapInfo;
import ie.gkenna.pennyk8s.models.DeploymentInfo;
import ie.gkenna.pennyk8s.models.NodeInfo;
import ie.gkenna.pennyk8s.models.PodInfo;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;
import okhttp3.Call;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class K8sService {

	private static final Logger LOGGER = LogManager.getLogger(K8sService.class);

	private final CoreV1Api coreV1Api;

	private final AppsV1Api appsV1Api;

	private final ApiClient client;

	public K8sService() throws IOException {
		this.client = Config.fromConfig("/home/gkenna/.kube/config");
		Configuration.setDefaultApiClient(client);
		this.coreV1Api = new CoreV1Api(client);
		this.appsV1Api = new AppsV1Api(client);
	}

	public List<NodeInfo> getAllNodes() {
		try {
			return coreV1Api.listNode(null, null, null, null, null, null, null, null, null, null)
				.getItems()
				.stream()
				.map(NodeInfo::fromNode)
				.collect(Collectors.toList());
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to get nodes", e);
		}
	}

	public List<PodInfo> getAllPods() {
		try {
			return coreV1Api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null)
				.getItems()
				.stream()
				.map(PodInfo::fromPod)
				.collect(Collectors.toList());
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to get pods", e);
		}
	}

	public List<ConfigMapInfo> getAllConfigMaps() {
		try {
			return coreV1Api.listConfigMapForAllNamespaces(null, null, null, null, null, null, null, null, null, null)
				.getItems()
				.stream()
				.map(ConfigMapInfo::fromConfigMap)
				.collect(Collectors.toList());
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to get ConfigMaps", e);
		}
	}

	public List<DeploymentInfo> getAllDeployments() {
		try {
			return appsV1Api.listDeploymentForAllNamespaces(null, null, null, null, null, null, null, null, null, null)
					.getItems()
					.stream()
					.map(DeploymentInfo::fromDeployment)
					.collect(Collectors.toList());
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to get Deployments", e);
		}
	}

	public String getPodLogs(String namespace, String podName) {
		try {
			return coreV1Api.readNamespacedPodLog(podName, namespace, null, null, null, null, null, null, null, null,
					null);
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to get logs for pod " + podName, e);
		}
	}

	public void watchPods(Consumer<Watch.Response<PodInfo>> onEvent) {
		try {
			watchResources(coreV1Api.listPodForAllNamespacesCall(null, null, null, null, null, null, null, null, 60,
					true, null), new TypeToken<Watch.Response<V1Pod>>() {
					}, PodInfo::fromPod, onEvent, "Pod");
		}
		catch (ApiException e) {
			throw new RuntimeException(e);
		}
	}

	public void watchNodes(Consumer<Watch.Response<NodeInfo>> onEvent) {
		try {
			watchResources(coreV1Api.listNodeCall(null, null, null, null, null, null, null, null, 60, true, null),
					new TypeToken<Watch.Response<V1Node>>() {
					}, NodeInfo::fromNode, onEvent, "Node");
		}
		catch (ApiException e) {
			throw new RuntimeException(e);
		}
	}

	public void watchDeployments(Consumer<Watch.Response<DeploymentInfo>> onEvent) {
		try {
			watchResources(appsV1Api.listDeploymentForAllNamespacesCall(null, null, null, null, null, null, null, null,
					60, true, null), new TypeToken<Watch.Response<V1Deployment>>() {
					}, DeploymentInfo::fromDeployment, onEvent, "Deployment");
		}
		catch (ApiException e) {
			throw new RuntimeException(e);
		}
	}

	public void watchConfigMaps(Consumer<Watch.Response<ConfigMapInfo>> onEvent) {
		try {
			watchResources(coreV1Api.listConfigMapForAllNamespacesCall(null, null, null, null, null, null, null, null,
					60, true, null), new TypeToken<Watch.Response<V1ConfigMap>>() {
					}, ConfigMapInfo::fromV1ConfigMap, onEvent, "ConfigMap");
		}
		catch (ApiException e) {
			throw new RuntimeException(e);
		}
	}

	private <T, R> void watchResources(Call call, TypeToken<Watch.Response<T>> typeToken, Function<T, R> mapper,
			Consumer<Watch.Response<R>> onEvent, String resourceName) {
		try (Watch<T> watch = Watch.createWatch(client, call, typeToken.getType())) {
			for (Watch.Response<T> item : watch) {
				R dto = mapper.apply(item.object);
				Watch.Response<R> event = new Watch.Response<>(item.type, dto);
				LOGGER.info("Publishing {} event: {} for {}", resourceName, event.type, dto.toString());
				onEvent.accept(event);
			}
		}
		catch (Exception e) {
			LOGGER.error("Error watching {}s", resourceName, e);
		}
	}

}
