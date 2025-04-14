import axios from 'axios'

export async function fetchNodes() {
    try {
        const response = await axios.get('/api/nodes')
        return response.data
    } catch (error) {
        console.error('Failed to fetch nodes:', error)
        return []
    }
}

export async function fetchPods() {
    try {
        const response = await axios.get('/api/pods')
        return response.data
    } catch (error) {
        console.error('Failed to fetch pods:', error)
        return []
    }
}

export async function fetchLogs(namespace, podName) {
    try {
        const response = await axios.get(`/api/pods/${namespace}/${podName}/logs`)
        return response.data
    } catch (error) {
        console.error(`Error fetching logs for ${podName}:`, error)
        return 'Error loading logs.'
    }
}

export async function fetchConfigMaps() {
    try {
        const response = await axios.get('/api/configmaps')
        return response.data
    } catch (error) {
        console.error('Failed to fetch configmaps:', error)
        return []
    }
}
