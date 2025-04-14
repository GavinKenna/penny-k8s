package ie.gkenna.pennyk8s.services;

import ie.gkenna.pennyk8s.models.ConfigMapInfo;
import ie.gkenna.pennyk8s.models.NodeInfo;
import ie.gkenna.pennyk8s.models.PodInfo;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class K8sService {
    private final CoreV1Api api;

    public K8sService() throws IOException {
        ApiClient client = Config.fromCluster();
        Configuration.setDefaultApiClient(client);
        this.api = new CoreV1Api(client);
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

    public String getPodLogs(String namespace, String podName) {
        try {
            return api.readNamespacedPodLog(podName, namespace, null, null, null, null, null, null, null, null, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get logs for pod " + podName, e);
        }
    }


}
