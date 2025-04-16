package ie.gkenna.pennyk8s.services;

import com.google.gson.reflect.TypeToken;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;
import ie.gkenna.pennyk8s.models.ConfigMapInfo;
import ie.gkenna.pennyk8s.models.NodeInfo;
import ie.gkenna.pennyk8s.models.PodInfo;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class K8sService {
    private final CoreV1Api api;
    private final ApiClient client;
    private static final Logger LOGGER =
            LogManager.getLogger(K8sService.class);

    public K8sService() throws IOException {
        client = Config.fromCluster();
        Configuration.setDefaultApiClient(client);
        api = new CoreV1Api(client);
    }

    public List<NodeInfo> getAllNodes() {
        try {
            return api.listNode(null, null, null, null, null, null, null, null, null, null)
                    .getItems().stream()
                    .map(NodeInfo::fromNode)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get nodes", e);
        }
    }

    public List<PodInfo> getAllPods() {
        try {
            return api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null)
                    .getItems().stream()
                    .map(PodInfo::fromPod)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get pods", e);
        }
    }

    public String getPodLogs(String namespace, String podName) {
        try {
            return api.readNamespacedPodLog(podName, namespace, null, null, null, null, null, null, null, null, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get logs for pod " + podName, e);
        }
    }

    public List<ConfigMapInfo> getAllConfigMaps() {
        try {
            return api.listConfigMapForAllNamespaces(null, null, null, null, null, null, null, null, null, null)
                    .getItems().stream()
                    .map(ConfigMapInfo::fromConfigMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get ConfigMaps", e);
        }
    }

    /**
     * Watch for ConfigMap events in the "default" namespace and convert them
     * to ConfigMapInfo objects.
     */
    public void watchConfigMaps(Consumer<Watch.Response<ConfigMapInfo>> onEvent) {
        try (Watch<V1ConfigMap> watch = Watch.createWatch(
                client,
                api.listNamespacedConfigMapCall(
                        "default",    // Change namespace as needed
                        null, null, null, null, null, null, null, null,10, Boolean.TRUE, null),
                new TypeToken<Watch.Response<V1ConfigMap>>(){}.getType()
        )) {
            for (Watch.Response<V1ConfigMap> item : watch) {
                // Convert V1ConfigMap to your ConfigMapInfo DTO
                ConfigMapInfo info = ConfigMapInfo.fromV1ConfigMap(item.object);
                // Create a new Watch.Response with the same event type and the DTO
                Watch.Response<ConfigMapInfo> event = new Watch.Response<>(item.type, info);
                LOGGER.info("Publishing configmap event: " + event.type + " for ConfigMap: " + info.getName());

                onEvent.accept(event);
            }
        } catch (Exception e) {
            System.err.println("Error watching ConfigMaps: " + e.getMessage());
            // In production, use a logger to log the error
        }
    }
}
