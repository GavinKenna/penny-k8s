<template>
  <div class="flex h-[calc(100vh-4rem)]">
    <!-- Services List -->
    <div class="w-1/2 p-4 overflow-y-auto border-r border-gray-200">
      <header class="mb-6 text-center">
        <h2 class="text-3xl font-bold text-blue-600">Services</h2>
        <p class="text-gray-600">A summary of your Services.</p>
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
            <th class="px-4 py-2 border">Type</th>
          </tr>
          </thead>
          <tbody>
          <tr
              v-for="d in services"
              :key="d.name"
              class="border-b hover:bg-gray-50"
              :class="{ 'bg-blue-50': selectedService?.name === d.name }"
          >
            <td class="px-4 py-2 border">{{ d.name }}</td>
            <td class="px-4 py-2 border">{{ d.namespace }}</td>
            <td class="px-4 py-2 border">{{ d.type }}</td>

            <td class="px-4 py-2 border text-center">
              <button
                  class="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600 transition"
                  @click="selectService(d)"
              >
                View
              </button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Service Detail Panel -->
    <div class="w-1/2 p-6 overflow-y-auto">
      <div v-if="selectedService">
        <h3 class="text-2xl font-bold mb-4 text-blue-700">
          Service: {{ selectedService.name }}
        </h3>
        <p class="mb-2">
          <strong>Namespace:</strong> {{ selectedService.namespace }}
        </p>
        <p class="mb-2">
          <strong>Type:</strong> {{ selectedService.type }}
        </p>
      </div>
      <div v-else class="text-gray-500 text-center mt-10">
        <p class="text-xl">Select a service to view details</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { pennyWebsockets } from '../composables/pennyWebsockets.js'

const selectedService = ref(null)
const errorMsg = ref(null)

const { services } = pennyWebsockets()

onMounted(async () => {
  try {
    const response = await axios.get('/api/services')
    if (services.value.length === 0) {
      services.value = response.data
    }
  } catch (error) {
    console.error('Error fetching services via REST:', error)
    errorMsg.value = 'Failed to load services'
  }
})

const selectService = (cm) => {
  selectedService.value = cm
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
