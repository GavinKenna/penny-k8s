package ie.gkenna.pennyk8s.controllers;

import ie.gkenna.pennyk8s.services.K8sService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MonitoringController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private K8sService k8sService;

    // Publish updates every 10 seconds. Adjust as needed.
    @Scheduled(fixedRate = 10)
    public void pushUpdates() {
        // Update nodes
        messagingTemplate.convertAndSend("/topic/nodes", k8sService.getAllNodes());
        // Update pods
        messagingTemplate.convertAndSend("/topic/pods", k8sService.getAllPods());

        messagingTemplate.convertAndSend("/topic/configmaps", k8sService.getAllConfigMaps());
    }
}