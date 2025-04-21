<template>
  <div class="flex h-[calc(100vh-4rem)]">
    <!-- Deployments List -->
    <div class="w-1/2 p-4 overflow-y-auto border-r border-gray-200">
      <header class="mb-6 text-center">
        <h2 class="text-3xl font-bold text-blue-600">Deployments</h2>
        <p class="text-gray-600">A summary of your Deployments.</p>
      </header>

      <div v-if="errorMsg" class="mb-4 text-red-600 text-center">
        {{ errorMsg }}
      </div>

      <div class="overflow-x-auto">
        <table class="min-w-full border-collapse">
          <thead>
          <tr class="bg-gray-100">
            <th class="px-4 py-2 border">Name</th>
            <th class="px-4 py-2 border">Namespace</th>
            <th class="px-4 py-2 border">Replicas</th>
            <th class="px-4 py-2 border">Available</th>
            <th class="px-4 py-2 border">Updated</th>
            <th class="px-4 py-2 border"># Containers</th>
            <th class="px-4 py-2 border text-center">Actions</th>
          </tr>
          </thead>
          <tbody>
          <tr
              v-for="d in deployments"
              :key="d.name"
              class="border-b hover:bg-gray-50"
              :class="{ 'bg-blue-50': selectedDeployment?.name === d.name }"
          >
            <td class="px-4 py-2 border">{{ d.name }}</td>
            <td class="px-4 py-2 border">{{ d.namespace }}</td>
            <td class="px-4 py-2 border">{{ d.replicas }}</td>
            <td class="px-4 py-2 border">{{ d.availableReplicas }}</td>
            <td class="px-4 py-2 border">{{ d.updatedReplicas }}</td>
            <td class="px-4 py-2 border">
              {{ d.containers ? Object.keys(d.containers).length : 0 }}
            </td>
            <td class="px-4 py-2 border text-center">
              <button
                  class="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600 transition"
                  @click="selectDeployment(d)"
              >
                View
              </button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Deployment Detail Panel -->
    <div class="w-1/2 p-6 overflow-y-auto">
      <div v-if="selectedDeployment">
        <h3 class="text-2xl font-bold mb-4 text-blue-700">
          Deployment: {{ selectedDeployment.name }}
        </h3>
        <p class="mb-2">
          <strong>Namespace:</strong> {{ selectedDeployment.namespace }}
        </p>
        <p class="mb-2">
          <strong>Replicas:</strong> {{ selectedDeployment.replicas }}
        </p>
        <p class="mb-2">
          <strong>Available Replicas:</strong> {{ selectedDeployment.availableReplicas }}
        </p>
        <p class="mb-4">
          <strong>Updated Replicas:</strong> {{ selectedDeployment.updatedReplicas }}
        </p>

        <div v-if="selectedDeployment.containers">
          <h4 class="text-xl font-semibold mb-2">Containers:</h4>
          <div
              v-for="(value, key) in selectedDeployment.containers"
              :key="key"
              class="mb-3"
          >
            <p class="font-medium">{{ key }}:</p>
            <pre class="bg-gray-100 p-2 border rounded text-sm overflow-auto whitespace-pre-wrap">
{{ value }}
            </pre>
          </div>
        </div>
      </div>
      <div v-else class="text-gray-500 text-center mt-10">
        <p class="text-xl">Select a deployment to view details</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { pennyWebsockets } from '../composables/pennyWebsockets.js'

const selectedDeployment = ref(null)
const errorMsg = ref(null)

const { deployments } = pennyWebsockets()

onMounted(async () => {
  try {
    const response = await axios.get('/api/deployments')
    if (deployments.value.length === 0) {
      deployments.value = response.data
    }
  } catch (error) {
    console.error('Error fetching deployments via REST:', error)
    errorMsg.value = 'Failed to load deployments'
  }
})

const selectDeployment = (cm) => {
  selectedDeployment.value = cm
}
</script>

<style scoped>
table {
  width: 100%;
  border-collapse: collapse;
}
th,
td {
  padding: 0.75rem;
  text-align: left;
  border: 1px solid #e5e7eb;
}
</style>
