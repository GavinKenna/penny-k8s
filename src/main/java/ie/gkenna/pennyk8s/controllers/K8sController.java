package ie.gkenna.pennyk8s.controllers;
import ie.gkenna.pennyk8s.services.K8sService;
import org.springframework.web.bind.annotation.*;
import ie.gkenna.pennyk8s.models.*;
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
}
