package ie.gkenna.pennyk8s.dto;

import ie.gkenna.pennyk8s.models.ConfigMapInfo;

public class ConfigMapEventDTO {
    private String eventType;
    private ConfigMapInfo configMap;

    public ConfigMapEventDTO(String eventType, ConfigMapInfo configMap) {
        this.eventType = eventType;
        this.configMap = configMap;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public ConfigMapInfo getConfigMap() {
        return configMap;
    }

    public void setConfigMap(ConfigMapInfo configMap) {
        this.configMap = configMap;
    }
}
