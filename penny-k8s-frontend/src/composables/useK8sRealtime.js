import { ref, onMounted, onUnmounted } from 'vue'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

export function useK8sRealtime() {
    const nodes = ref([])
    const pods = ref([])
    const configMaps = ref([])

    let stompClient = null

    const connectWebSocket = () => {
        const socket = new SockJS("/ws")
        stompClient = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            onConnect: () => {
                console.log("WebSocket connected.")

                stompClient.subscribe('/topic/nodes', message => {
                    try {
                        const event = JSON.parse(message.body)

                        if (event.eventType === 'ADDED') {
                            if (!nodes.value.some(cm => cm.name === event.Node.name)) {
                                nodes.value = [...nodes.value, event.Node]
                            }
                        } else if (event.eventType === 'MODIFIED') {
                            nodes.value = nodes.value.map(cm =>
                                cm.name === event.Node.name ? event.Node : cm
                            )
                        } else if (event.eventType === 'DELETED') {
                            nodes.value = nodes.value.filter(cm => cm.name !== event.Node.name)
                        }

                        console.log("Updated Nodes:", nodes.value)
                    } catch (e) {
                        console.error('Error parsing Nodes message', e)
                    }
                })

                stompClient.subscribe('/topic/pods', message => {
                    try {
                        const event = JSON.parse(message.body)

                        if (event.eventType === 'ADDED') {
                            if (!pods.value.some(cm => cm.name === event.pod.name)) {
                                pods.value = [...pods.value, event.pod]
                            }
                        } else if (event.eventType === 'MODIFIED') {
                            pods.value = pods.value.map(cm =>
                                cm.name === event.pod.name ? event.pod : cm
                            )
                        } else if (event.eventType === 'DELETED') {
                            pods.value = pods.value.filter(cm => cm.name !== event.pod.name)
                        }

                        console.log("Updated Pods:", pods.value)
                    } catch (e) {
                        console.error('Error parsing Pods message', e)
                    }
                })

                stompClient.subscribe('/topic/configmaps', message => {
                    try {
                        const event = JSON.parse(message.body)

                        if (event.eventType === 'ADDED') {
                            if (!configMaps.value.some(cm => cm.name === event.configMap.name)) {
                                configMaps.value = [...configMaps.value, event.configMap]
                            }
                        } else if (event.eventType === 'MODIFIED') {
                            configMaps.value = configMaps.value.map(cm =>
                                cm.name === event.configMap.name ? event.configMap : cm
                            )
                        } else if (event.eventType === 'DELETED') {
                            configMaps.value = configMaps.value.filter(cm => cm.name !== event.configMap.name)
                        }

                        console.log("Updated configMaps:", configMaps.value)
                    } catch (e) {
                        console.error('Error parsing configmaps message', e)
                    }
                })
            },
            debug: (str) => console.log(str)
        })

        stompClient.activate()
    }

    onMounted(() => {
        connectWebSocket()
    })

    onUnmounted(() => {
        if (stompClient) {
            stompClient.deactivate()
        }
    })

    return { nodes, pods, configMaps }
}
