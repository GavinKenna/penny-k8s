package ie.gkenna.pennyk8s.backend.controllers;

import ie.gkenna.pennyk8s.backend.dto.ResourceEventDTO;
import ie.gkenna.pennyk8s.backend.models.ConfigMapInfo;
import ie.gkenna.pennyk8s.backend.models.DeploymentInfo;
import ie.gkenna.pennyk8s.backend.models.NamespaceInfo;
import ie.gkenna.pennyk8s.backend.models.NodeInfo;
import ie.gkenna.pennyk8s.backend.models.PodInfo;
import ie.gkenna.pennyk8s.backend.services.PennyService;
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

	private static final Logger LOGGER = LogManager.getLogger(MonitoringController.class);

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private PennyService pennyService;

	@PostConstruct
	public void initWatch() {
		startWatchThread("ConfigMap", "/topic/configmaps", () -> pennyService.watchConfigMaps(event -> messagingTemplate
			.convertAndSend("/topic/configmaps", new ResourceEventDTO<>(event.type, event.object))));

		startWatchThread("Pod", "/topic/pods", () -> pennyService.watchPods(event -> messagingTemplate
			.convertAndSend("/topic/pods", new ResourceEventDTO<>(event.type, event.object))));

		startWatchThread("Node", "/topic/nodes", () -> pennyService.watchNodes(event -> messagingTemplate
			.convertAndSend("/topic/nodes", new ResourceEventDTO<>(event.type, event.object))));

		startWatchThread("Deployment", "/topic/deployments",
				() -> pennyService.watchDeployments(event -> messagingTemplate.convertAndSend("/topic/deployments",
						new ResourceEventDTO<>(event.type, event.object))));

		startWatchThread("Service", "/topic/Services",
				() -> pennyService.watchServices(event -> messagingTemplate.convertAndSend("/topic/Services",
						new ResourceEventDTO<>(event.type, event.object))));

		startWatchThread("Namespace", "/topic/namespaces", () -> pennyService.watchNamespaces(event -> messagingTemplate
			.convertAndSend("/topic/namespaces", new ResourceEventDTO<>(event.type, event.object))));
	}

	private void startWatchThread(String resourceType, String topic, Runnable watchTask) {
		new Thread(() -> {
			while (true) {
				try {
					LOGGER.info("Starting {} watch...", resourceType);
					watchTask.run();
					LOGGER.warn("{} watch exited, restarting...", resourceType);
				}
				catch (Exception e) {
					LOGGER.error("Exception in {} watch", resourceType, e);
				}

				try {
					Thread.sleep(3000);
				}
				catch (InterruptedException ignored) {
				}
			}
		}, resourceType.toLowerCase() + "-watch-thread").start();
	}

	private void runWatch(String resourceType, String topic, Runnable watchTask) {
		try {
			LOGGER.info("Starting {} watch...", resourceType);
			watchTask.run();
			LOGGER.warn("{} watch exited, restarting...", resourceType);
		}
		catch (Exception e) {
			LOGGER.error("Exception in {} watch", resourceType, e);
		}
		try {
			Thread.sleep(3_000);
		}
		catch (InterruptedException ignored) {
		}
	}

	@Scheduled(fixedRate = 10_000)
	public void publishNodesAndPods() {
		messagingTemplate.convertAndSend("/topic/nodes", pennyService.getAllNodes());
		messagingTemplate.convertAndSend("/topic/pods", pennyService.getAllPods());
	}

}
