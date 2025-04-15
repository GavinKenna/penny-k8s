import { ref, onMounted, onUnmounted } from 'vue'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

export function useK8sRealtime() {
    const nodes = ref([])
    const pods = ref([])
    const configMaps = ref([])
    let stompClient = null

    const connectWebSocket = () => {
        const socket = new SockJS("/ws"); // ✅ relative path - goes through Vite proxy
        stompClient = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 500,
            onConnect: () => {
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
                        configMaps.value = JSON.parse(message.body)
                    } catch (e) {
                        console.error('Error parsing configmapss message', e)
                    }
                })
            },
            debug: str => console.log(str)
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
