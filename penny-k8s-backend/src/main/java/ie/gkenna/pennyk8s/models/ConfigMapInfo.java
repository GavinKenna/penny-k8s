package ie.gkenna.pennyk8s.models;

import io.kubernetes.client.openapi.models.V1ConfigMap;

import java.util.Map;

public class ConfigMapInfo {
    public String name;
    public String namespace;
    public String creationTimestamp;
    public Map<String, String> data;


    public static ConfigMapInfo fromConfigMap(V1ConfigMap configMap) {
        ConfigMapInfo info = new ConfigMapInfo();
        info.name = configMap.getMetadata().getName();
        info.namespace = configMap.getMetadata().getNamespace();
        info.creationTimestamp = String.valueOf(configMap.getMetadata().getCreationTimestamp());
        info.data = configMap.getData();
        return info;
    }


}