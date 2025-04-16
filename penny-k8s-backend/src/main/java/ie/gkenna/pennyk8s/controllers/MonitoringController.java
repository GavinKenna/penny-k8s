package ie.gkenna.pennyk8s.controllers;

import ie.gkenna.pennyk8s.dto.ConfigMapEventDTO;
import ie.gkenna.pennyk8s.services.K8sService;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableScheduling
public class MonitoringController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private K8sService k8sService;

    private static final Logger LOGGER =
            LogManager.getLogger(MonitoringController.class);

    /**
     * Use a separate thread to run the watch so it doesn’t block the scheduled tasks.
     * This method will publish incremental ConfigMap events in real time.
     */
    @PostConstruct
    public void initWatch() {
        new Thread(() -> {
            while (true) {
                try {
                    LOGGER.info("Starting ConfigMap watch...");
                    k8sService.watchConfigMaps(event -> {
                        ConfigMapEventDTO dto = new ConfigMapEventDTO(event.type, event.object);
                        LOGGER.info("Sending ConfigMap event via WebSocket: " + dto.getEventType() + " for " + dto.getConfigMap().getName());
                        messagingTemplate.convertAndSend("/topic/configmaps", dto);
                    });
                    LOGGER.warn("ConfigMap watch exited, restarting...");
                } catch (Exception e) {
                    LOGGER.error("Exception in ConfigMap watch thread", e);
                }

                try {
                    Thread.sleep(3000); // avoid tight loop on failures
                } catch (InterruptedException ignored) {
                }
            }
        }, "configmap-watch-thread").start();
    }

    // Publish nodes and pods updates every 10 seconds (or adjust as needed)
    @Scheduled(fixedRate = 10000)
    public void publishNodesAndPods() {
        messagingTemplate.convertAndSend("/topic/nodes", k8sService.getAllNodes());
        messagingTemplate.convertAndSend("/topic/pods", k8sService.getAllPods());
    }
}
