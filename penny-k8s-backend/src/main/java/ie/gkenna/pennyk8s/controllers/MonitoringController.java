package ie.gkenna.pennyk8s.controllers;

import ie.gkenna.pennyk8s.dto.ConfigMapEventDTO;
import ie.gkenna.pennyk8s.dto.NodeEventDTO;
import ie.gkenna.pennyk8s.dto.PodEventDTO;
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
                watchConfigMaps();
                watchNodes();
                watchPods();
            }
        }, "configmap-watch-thread").start();
    }

    private void watchConfigMaps() {
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

    private void watchPods() {
        try {
            LOGGER.info("Starting Pod watch...");
            k8sService.watchPods(event -> {
                PodEventDTO dto = new PodEventDTO(event.type, event.object);
                LOGGER.info("Sending Pod event via WebSocket: " + dto.getEventType() + " for " + dto.getPod().getName());
                messagingTemplate.convertAndSend("/topic/pods", dto);
            });
            LOGGER.warn("Pod watch exited, restarting...");
        } catch (Exception e) {
            LOGGER.error("Exception in Pod watch thread", e);
        }

        try {
            Thread.sleep(3000); // avoid tight loop on failures
        } catch (InterruptedException ignored) {
        }
    }

    private void watchNodes() {
        try {
            LOGGER.info("Starting Node watch...");
            k8sService.watchNodes(event -> {
                NodeEventDTO dto = new NodeEventDTO(event.type, event.object);
                LOGGER.info("Sending Node event via WebSocket: " + dto.getEventType() + " for " + dto.getNode().getName());
                messagingTemplate.convertAndSend("/topic/nodes", dto);
            });
            LOGGER.warn("Node watch exited, restarting...");
        } catch (Exception e) {
            LOGGER.error("Exception in Node watch thread", e);
        }

        try {
            Thread.sleep(3000); // avoid tight loop on failures
        } catch (InterruptedException ignored) {
        }
    }

    // Publish nodes and pods updates every 10 seconds (or adjust as needed)
    @Scheduled(fixedRate = 10000)
    public void publishNodesAndPods() {
        messagingTemplate.convertAndSend("/topic/nodes", k8sService.getAllNodes());
        messagingTemplate.convertAndSend("/topic/pods", k8sService.getAllPods());
    }
}
