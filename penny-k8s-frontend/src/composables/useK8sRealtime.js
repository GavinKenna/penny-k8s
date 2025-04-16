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
                        nodes.value = JSON.parse(message.body)
                    } catch (e) {
                        console.error('Error parsing nodes message', e)
                    }
                })

                stompClient.subscribe('/topic/pods', message => {
                    try {
                        pods.value = JSON.parse(message.body)
                    } catch (e) {
                        console.error('Error parsing pods message', e)
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
