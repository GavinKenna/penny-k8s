package ie.gkenna.pennyk8s.controllers;

import ie.gkenna.pennyk8s.models.ConfigMapInfo;
import ie.gkenna.pennyk8s.models.NodeInfo;
import ie.gkenna.pennyk8s.models.PodInfo;
import ie.gkenna.pennyk8s.services.K8sService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class K8sController {

	private final K8sService k8sService;

	public K8sController(K8sService k8sService) {
		this.k8sService = k8sService;
	}

	@GetMapping("/nodes")
	public List<NodeInfo> getNodes() {
		return k8sService.getAllNodes();
	}

	@GetMapping("/pods")
	public List<PodInfo> getPods() {
		return k8sService.getAllPods();
	}

	@GetMapping("/configmaps")
	public List<ConfigMapInfo> getConfigMapInfos() {
		return k8sService.getAllConfigMaps();
	}

	@GetMapping("/pods/{namespace}/{podName}/logs")
	public String getPodLogs(@PathVariable String namespace, @PathVariable String podName) {
		return k8sService.getPodLogs(namespace, podName);
	}

}
