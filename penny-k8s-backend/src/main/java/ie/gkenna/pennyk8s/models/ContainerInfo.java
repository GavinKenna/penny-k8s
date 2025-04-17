package ie.gkenna.pennyk8s.models;

import io.kubernetes.client.openapi.models.V1Container;

import java.util.List;

public class ContainerInfo {

	public String name;

	public String image;

	public static List<ContainerInfo> transform(List<V1Container> containers) {
		return null;
	}

	public static ContainerInfo fromContainer(V1Container container) {
		ContainerInfo info = new ContainerInfo();
		info.name = container.getName();
		info.image = container.getImage();
		return info;
	}

}
