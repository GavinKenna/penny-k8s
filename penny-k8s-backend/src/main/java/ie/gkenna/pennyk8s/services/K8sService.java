package ie.gkenna.pennyk8s.services;

import com.google.gson.reflect.TypeToken;
import ie.gkenna.pennyk8s.models.ConfigMapInfo;
import ie.gkenna.pennyk8s.models.NodeInfo;
import ie.gkenna.pennyk8s.models.PodInfo;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
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
    private final CoreV1Api api;
    private final ApiClient client;

    public K8sService() throws IOException {
        this.client = Config.fromCluster();
        Configuration.setDefaultApiClient(client);
        this.api = new CoreV1Api(client);
    }

    public List<NodeInfo> getAllNodes() {
        try {
            return api.listNode(null, null, null, null, null, null, null, null, null, null)
                    .getItems()
                    .stream()
                    .map(NodeInfo::fromNode)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get nodes", e);
        }
    }

    public List<PodInfo> getAllPods() {
        try {
            return api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null)
                    .getItems()
                    .stream()
                    .map(PodInfo::fromPod)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get pods", e);
        }
    }

    public List<ConfigMapInfo> getAllConfigMaps() {
        try {
            return api.listConfigMapForAllNamespaces(null, null, null, null, null, null, null, null, null, null)
                    .getItems()
                    .stream()
                    .map(ConfigMapInfo::fromConfigMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get ConfigMaps", e);
        }
    }

    public String getPodLogs(String namespace, String podName) {
        try {
            return api.readNamespacedPodLog(podName, namespace, null, null, null, null, null, null, null, null, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get logs for pod " + podName, e);
        }
    }

    public void watchPods(Consumer<Watch.Response<PodInfo>> onEvent)  {
        try {
            watchResources(
                    api.listPodForAllNamespacesCall(null, null, null, null, null, null, null, null, 60, true, null),
                    new TypeToken<Watch.Response<V1Pod>>() {},
                    PodInfo::fromPod,
                    onEvent,
                    "Pod"
            );
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void watchNodes(Consumer<Watch.Response<NodeInfo>> onEvent) {
        try {
            watchResources(
                    api.listNodeCall(null, null, null, null, null, null, null, null, 60, true, null),
                    new TypeToken<Watch.Response<V1Node>>() {},
                    NodeInfo::fromNode,
                    onEvent,
                    "Node"
            );
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void watchConfigMaps(Consumer<Watch.Response<ConfigMapInfo>> onEvent)  {
        try {
            watchResources(
                    api.listConfigMapForAllNamespacesCall(null, null, null, null, null, null, null, null, 60, true, null),
                    new TypeToken<Watch.Response<V1ConfigMap>>() {},
                    ConfigMapInfo::fromV1ConfigMap,
                    onEvent,
                    "ConfigMap"
            );
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private <T, R> void watchResources(
            Call call,
            TypeToken<Watch.Response<T>> typeToken,
            Function<T, R> mapper,
            Consumer<Watch.Response<R>> onEvent,
            String resourceName
    ) {
        try (Watch<T> watch = Watch.createWatch(client, call, typeToken.getType())) {
            for (Watch.Response<T> item : watch) {
                R dto = mapper.apply(item.object);
                Watch.Response<R> event = new Watch.Response<>(item.type, dto);
                LOGGER.info("Publishing {} event: {} for {}", resourceName, event.type, dto.toString());
                onEvent.accept(event);
            }
        } catch (Exception e) {
            LOGGER.error("Error watching {}s", resourceName, e);
        }
    }
}
