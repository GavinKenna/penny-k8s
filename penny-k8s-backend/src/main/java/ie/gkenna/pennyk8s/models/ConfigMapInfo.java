package ie.gkenna.pennyk8s.models;

import io.kubernetes.client.openapi.models.V1ConfigMap;

import java.util.Map;

public class ConfigMapInfo {
    private String name;
    private String namespace;
    private String creationTimestamp;
    private java.util.Map<String, String> data;

    // Getters and setters...

    public static ConfigMapInfo fromV1ConfigMap(V1ConfigMap cm) {
        ConfigMapInfo info = new ConfigMapInfo();
        info.setName(cm.getMetadata().getName());
        info.setNamespace(cm.getMetadata().getNamespace());
        info.setCreationTimestamp(cm.getMetadata().getCreationTimestamp().toString());
        info.setData(cm.getData());
        return info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(String creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public static ConfigMapInfo fromConfigMap(V1ConfigMap configMap) {
        ConfigMapInfo info = new ConfigMapInfo();
        info.name = configMap.getMetadata().getName();
        info.namespace = configMap.getMetadata().getNamespace();
        info.creationTimestamp = String.valueOf(configMap.getMetadata().getCreationTimestamp());
        info.data = configMap.getData();
        return info;
    }
}
