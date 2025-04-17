/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

	private static final Logger LOGGER = LogManager.getLogger(MonitoringController.class);

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private K8sService k8sService;

	@PostConstruct
	public void initWatch() {
		new Thread(() -> {

			while (true) {
				this.runWatch("ConfigMap", "/topic/configmaps",
						() -> k8sService.watchConfigMaps(event -> messagingTemplate.convertAndSend("/topic/configmaps",
								new ConfigMapEventDTO(event.type, event.object))));

				this.runWatch("Pod", "/topic/pods", () -> k8sService.watchPods(event -> messagingTemplate
					.convertAndSend("/topic/pods", new PodEventDTO(event.type, event.object))));

				this.runWatch("Node", "/topic/nodes", () -> k8sService.watchNodes(event -> messagingTemplate
					.convertAndSend("/topic/nodes", new NodeEventDTO(event.type, event.object))));
			}

		}, "k8s-watch-thread").start();
	}

	private void runWatch(String resourceType, String topic, Runnable watchTask) {
		try {
			LOGGER.info("Starting {} watch...", resourceType);
			watchTask.run();
			LOGGER.warn("{} watch exited, restarting...", resourceType);
		}
		catch (Exception e) {
			LOGGER.error("Exception in {} watch thread", resourceType, e);
		}

		try {
			Thread.sleep(3000);
		}
		catch (InterruptedException ignored) {
		}
	}

	@Scheduled(fixedRate = 10000)
	public void publishNodesAndPods() {
		messagingTemplate.convertAndSend("/topic/nodes", k8sService.getAllNodes());
		messagingTemplate.convertAndSend("/topic/pods", k8sService.getAllPods());
	}

}
