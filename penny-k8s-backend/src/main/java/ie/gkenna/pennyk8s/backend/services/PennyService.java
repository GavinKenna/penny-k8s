package ie.gkenna.pennyk8s.backend.services;

import com.google.gson.reflect.TypeToken;
import ie.gkenna.pennyk8s.backend.models.*;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.RbacAuthorizationV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;
import okhttp3.Call;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PennyService {

	private static final Logger LOGGER = LogManager.getLogger(PennyService.class);

	private final CoreV1Api coreV1Api;

	private final AppsV1Api appsV1Api;

	private final RbacAuthorizationV1Api rbacApi;

	private final ApiClient client;

	public PennyService(@Value("${k8s.useInClusterConfig}") boolean useInClusterConfig,
			@Value("${k8s.kubeConfigPath}") String kubeConfigPath) throws IOException {
		// pick in‑cluster vs. kubeconfig
		client = useInClusterConfig ? Config.fromCluster() : Config.fromConfig(kubeConfigPath);
		Configuration.setDefaultApiClient(client);
		coreV1Api = new CoreV1Api(client);
		appsV1Api = new AppsV1Api(client);
		rbacApi = new RbacAuthorizationV1Api(client);
	}

	public List<NodeInfo> getAllNodes() {
		try {
			V1NodeList list = coreV1Api.listNode() // builder
				.execute(); // run
			return list.getItems().stream().map(NodeInfo::fromNode).collect(Collectors.toList());
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to list nodes", e);
		}
	}

	public List<PodInfo> getAllPods() {
		try {
			V1PodList list = coreV1Api.listPodForAllNamespaces().execute();
			return list.getItems().stream().map(PodInfo::fromPod).collect(Collectors.toList());
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to list pods", e);
		}
	}

	public List<NamespaceInfo> getAllNamespaces() {
		try {
			V1NamespaceList list = coreV1Api.listNamespace().execute();
			return list.getItems().stream().map(NamespaceInfo::fromNamespace).collect(Collectors.toList());
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to list namespaces", e);
		}
	}

	public List<ConfigMapInfo> getAllConfigMaps() {
		try {
			V1ConfigMapList list = coreV1Api.listConfigMapForAllNamespaces().execute();
			return list.getItems().stream().map(ConfigMapInfo::fromConfigMap).collect(Collectors.toList());
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to list configmaps", e);
		}
	}

	public List<DeploymentInfo> getAllDeployments() {
		try {
			V1DeploymentList list = appsV1Api.listDeploymentForAllNamespaces().execute();
			return list.getItems().stream().map(DeploymentInfo::fromDeployment).collect(Collectors.toList());
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to list deployments", e);
		}
	}

	public List<ServiceInfo> getAllServices() {
		try {
			V1ServiceList list = coreV1Api.listServiceForAllNamespaces().execute();
			return list.getItems().stream().map(ServiceInfo::fromService).collect(Collectors.toList());
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to list services", e);
		}
	}

	public List<RoleBindingInfo> getAllRoleBindings() {
		try {
			V1RoleBindingList list = rbacApi.listRoleBindingForAllNamespaces().execute();
			return list.getItems().stream().map(RoleBindingInfo::fromRoleBinding).collect(Collectors.toList());
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to list roleBindings", e);
		}
	}

	public List<RoleInfo> getAllRoles() {
		try {
			V1RoleList list = rbacApi.listRoleForAllNamespaces().execute();
			return list.getItems().stream().map(RoleInfo::fromRole).collect(Collectors.toList());
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to list roles", e);
		}
	}

	public List<SecretInfo> getAllSecrets() {
		try {
			V1SecretList list = coreV1Api.listSecretForAllNamespaces().execute();
			return list.getItems().stream().map(SecretInfo::fromSecret).collect(Collectors.toList());
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to list secrets", e);
		}
	}

	public List<StatefulSetInfo> getAllStatefulSets() {
		try {
			V1StatefulSetList list = appsV1Api.listStatefulSetForAllNamespaces().execute();
			return list.getItems().stream().map(StatefulSetInfo::fromStatefulSet).collect(Collectors.toList());
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to list statefulSets", e);
		}
	}

	public List<ClusterRoleInfo> getAllClusterRoles() {
		try {
			V1ClusterRoleList list = rbacApi.listClusterRole().execute();
			return list.getItems().stream().map(ClusterRoleInfo::fromClusterRole).collect(Collectors.toList());
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to list clusterRoles", e);
		}
	}

	/**
	 * Read logs of a single Pod.
	 */
	public String getPodLogs(String namespace, String podName) {
		try {
			return coreV1Api.readNamespacedPodLog(podName, namespace) // builder
				.container(null)
				.follow(false)
				.pretty(null)
				.previous(false)
				.sinceSeconds(null)
				// .sinceTime(null)
				.tailLines(null)
				.timestamps(false)
				.limitBytes(null)
				.execute(); // runs and returns the log String
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to get logs for pod " + podName, e);
		}
	}

	// stream watchers for nodes, pods, etc

	public void watchNodes(Consumer<Watch.Response<NodeInfo>> onEvent) {
		try {
			CoreV1Api.APIlistNodeRequest req = coreV1Api.listNode().watch(true).timeoutSeconds(60);
			Call call = req.buildCall(null);
			watchResources(call, new TypeToken<Watch.Response<V1Node>>() {
			}.getType(), NodeInfo::fromNode, onEvent, "Node");
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to watch nodes", e);
		}
	}

	public void watchPods(Consumer<Watch.Response<PodInfo>> onEvent) {
		try {
			CoreV1Api.APIlistPodForAllNamespacesRequest req = coreV1Api.listPodForAllNamespaces()
				.watch(true)
				.timeoutSeconds(60);
			Call call = req.buildCall(null);
			watchResources(call, new TypeToken<Watch.Response<V1Pod>>() {
			}.getType(), PodInfo::fromPod, onEvent, "Pod");
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to watch pods", e);
		}
	}

	public void watchNamespaces(Consumer<Watch.Response<NamespaceInfo>> onEvent) {
		try {
			CoreV1Api.APIlistNamespaceRequest req = coreV1Api.listNamespace().watch(true).timeoutSeconds(60);
			Call call = req.buildCall(null);
			watchResources(call, new TypeToken<Watch.Response<V1Namespace>>() {
			}.getType(), NamespaceInfo::fromNamespace, onEvent, "Namespace");
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to watch namespaces", e);
		}
	}

	public void watchConfigMaps(Consumer<Watch.Response<ConfigMapInfo>> onEvent) {
		try {
			CoreV1Api.APIlistConfigMapForAllNamespacesRequest req = coreV1Api.listConfigMapForAllNamespaces()
				.watch(true)
				.timeoutSeconds(60);
			Call call = req.buildCall(null);
			watchResources(call, new TypeToken<Watch.Response<V1ConfigMap>>() {
			}.getType(), ConfigMapInfo::fromConfigMap, onEvent, "ConfigMap");
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to watch configmaps", e);
		}
	}

	public void watchDeployments(Consumer<Watch.Response<DeploymentInfo>> onEvent) {
		try {
			AppsV1Api.APIlistDeploymentForAllNamespacesRequest req = appsV1Api.listDeploymentForAllNamespaces()
				.watch(true)
				.timeoutSeconds(60);
			Call call = req.buildCall(null);
			watchResources(call, new TypeToken<Watch.Response<V1Deployment>>() {
			}.getType(), DeploymentInfo::fromDeployment, onEvent, "Deployment");
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to watch deployments", e);
		}
	}

	public void watchServices(Consumer<Watch.Response<ServiceInfo>> onEvent) {
		try {
			CoreV1Api.APIlistServiceForAllNamespacesRequest req = coreV1Api.listServiceForAllNamespaces()
				.watch(true)
				.timeoutSeconds(60);
			Call call = req.buildCall(null);
			watchResources(call, new TypeToken<Watch.Response<V1Service>>() {
			}.getType(), ServiceInfo::fromService, onEvent, "Service");
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to watch services", e);
		}
	}

	public void watchStatefulSets(Consumer<Watch.Response<StatefulSetInfo>> onEvent) {
		try {
			AppsV1Api.APIlistStatefulSetForAllNamespacesRequest req = appsV1Api.listStatefulSetForAllNamespaces()
				.watch(true)
				.timeoutSeconds(60);
			Call call = req.buildCall(null);
			watchResources(call, new TypeToken<Watch.Response<V1StatefulSet>>() {
			}.getType(), StatefulSetInfo::fromStatefulSet, onEvent, "StatefulSet");
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to watch statefulSets", e);
		}
	}

	public void watchClusterRoles(Consumer<Watch.Response<ClusterRoleInfo>> onEvent) {
		try {
			RbacAuthorizationV1Api.APIlistClusterRoleRequest req = rbacApi.listClusterRole()
				.watch(true)
				.timeoutSeconds(60);
			Call call = req.buildCall(null);
			watchResources(call, new TypeToken<Watch.Response<V1ClusterRole>>() {
			}.getType(), ClusterRoleInfo::fromClusterRole, onEvent, "ClusterRole");
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to watch clusterRoles", e);
		}
	}

	public void watchRoleBindings(Consumer<Watch.Response<RoleBindingInfo>> onEvent) {
		try {
			RbacAuthorizationV1Api.APIlistRoleBindingForAllNamespacesRequest req = rbacApi
				.listRoleBindingForAllNamespaces()
				.watch(true)
				.timeoutSeconds(60);
			Call call = req.buildCall(null);
			watchResources(call, new TypeToken<Watch.Response<V1RoleBinding>>() {
			}.getType(), RoleBindingInfo::fromRoleBinding, onEvent, "RoleBinding");
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to watch roleBindings", e);
		}
	}

	public void watchRoles(Consumer<Watch.Response<RoleInfo>> onEvent) {
		try {
			RbacAuthorizationV1Api.APIlistRoleForAllNamespacesRequest req = rbacApi.listRoleForAllNamespaces()
				.watch(true)
				.timeoutSeconds(60);
			Call call = req.buildCall(null);
			watchResources(call, new TypeToken<Watch.Response<V1Role>>() {
			}.getType(), RoleInfo::fromRole, onEvent, "Role");
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to watch roles", e);
		}
	}

	public void watchSecrets(Consumer<Watch.Response<SecretInfo>> onEvent) {
		try {
			CoreV1Api.APIlistSecretForAllNamespacesRequest req = coreV1Api.listSecretForAllNamespaces()
				.watch(true)
				.timeoutSeconds(60);
			Call call = req.buildCall(null);
			watchResources(call, new TypeToken<Watch.Response<V1Secret>>() {
			}.getType(), SecretInfo::fromSecret, onEvent, "Secret");
		}
		catch (ApiException e) {
			throw new RuntimeException("Failed to watch secrets", e);
		}
	}

	/**
	 * Helper that spins over the watch‐stream
	 */
	private <T, R> void watchResources(Call call, Type responseType, Function<T, R> mapper,
			Consumer<Watch.Response<R>> onEvent, String resourceName) {
		try (Watch<T> watch = Watch.createWatch(client, call, responseType)) {
			for (Watch.Response<T> item : watch) {
				R dto = mapper.apply(item.object);
				Watch.Response<R> ev = new Watch.Response<>(item.type, dto);
				LOGGER.info("Publishing {} event: {} for {}", resourceName, ev.type, dto);
				onEvent.accept(ev);
			}
		}
		catch (Exception e) {
			LOGGER.error("Error watching " + resourceName, e);
		}
	}

}
