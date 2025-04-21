import { ref } from 'vue'
import axios from 'axios'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

const nodes       = ref([])
const pods        = ref([])
const deployments = ref([])
const namespaces  = ref([])
const configMaps  = ref([])

let stompClient = null
let isConnected = false
let hasInitialized = false

const loadInitial = async () => {
  try {
    const [nRes, pRes, dRes, nsRes, cmRes] = await Promise.all([
      axios.get('/api/nodes'),
      axios.get('/api/pods'),
      axios.get('/api/deployments'),
      axios.get('/api/namespaces'),
      axios.get('/api/configmaps'),
    ])
    nodes.value       = nRes.data
    pods.value        = pRes.data
    deployments.value = dRes.data
    namespaces.value  = nsRes.data
    configMaps.value  = cmRes.data
  } catch (err) {
    console.error('Initial Kubernetes load failed', err)
  }
}

const connectWebSocket = () => {
  if (isConnected || stompClient) return

  const socket = new SockJS('/ws')

  stompClient = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    onConnect: () => {
      console.log('WebSocket connected.')
      isConnected = true

      const applyEvent = refList => message => {
        let payload
        try {
          payload = JSON.parse(message.body)
        } catch (e) {
          console.error('WS JSON parse failed:', message.body)
          return
        }

        if (Array.isArray(payload)) {
          refList.value = payload
          return
        }

        const ev = payload
        const obj = ev.resource
            || ev.object
            || ev.node
            || ev.pod
            || ev.deployment
            || ev.namespace
            || ev.configMap

        if (!ev.eventType || !obj || !obj.name) {
          console.warn('Unexpected WS event shape:', payload)
          return
        }

        if (ev.eventType === 'ADDED') {
          if (!refList.value.find(i => i.name === obj.name)) {
            refList.value.push(obj)
          }
        } else if (ev.eventType === 'MODIFIED') {
          refList.value = refList.value.map(i =>
              i.name === obj.name ? obj : i
          )
        } else if (ev.eventType === 'DELETED') {
          refList.value = refList.value.filter(i =>
              i.name !== obj.name
          )
        }
      }

      stompClient.subscribe('/topic/nodes',       applyEvent(nodes))
      stompClient.subscribe('/topic/pods',        applyEvent(pods))
      stompClient.subscribe('/topic/deployments', applyEvent(deployments))
      stompClient.subscribe('/topic/namespaces',  applyEvent(namespaces))
      stompClient.subscribe('/topic/configmaps',  applyEvent(configMaps))
    },
    onDisconnect: () => {
      console.log('WebSocket disconnected.')
      isConnected = false
    },
    onWebSocketClose: () => {
      console.log('WebSocket closed.')
      isConnected = false
    },
    debug: msg => console.debug('[STOMP]', msg),
  })

  stompClient.activate()
}

const init = async () => {
  if (hasInitialized) return
  hasInitialized = true
  await loadInitial()
  connectWebSocket()
}

export function pennyWebsockets() {
  init() // kick things off only once globally

  return {
    nodes,
    pods,
    deployments,
    namespaces,
    configMaps,
  }
}
