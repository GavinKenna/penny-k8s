<template>
  <div class="p-4">
    <!-- Page Header -->
    <header class="mb-6 text-center">
      <h2 class="text-3xl font-bold text-blue-600">Deployments</h2>
      <p class="text-gray-600">A summary of your Deployments.</p>
    </header>

    <!-- Deployments Table -->
    <div class="overflow-x-auto">
      <table class="min-w-full border-collapse">
        <thead>
          <tr class="bg-gray-100">
            <th class="px-4 py-2 border">Name</th>
            <th class="px-4 py-2 border">Namespace</th>
            <th class="px-4 py-2 border">Replicas</th>
            <th class="px-4 py-2 border">Availble Replicas</th>
            <th class="px-4 py-2 border">Updated Replicas</th>
            <th class="px-4 py-2 border"># of Containers</th>
            <th class="px-4 py-2 border text-center">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="cm in deployments"
            :key="cm.name"
            class="border-b hover:bg-gray-50 cursor-pointer"
          >
            <td class="px-4 py-2 border">{{ cm.name }}</td>
            <td class="px-4 py-2 border">{{ cm.namespace }}</td>
            <td class="px-4 py-2 border">{{ cm.replicas }}</td>
            <td class="px-4 py-2 border">{{ cm.availableReplicas }}</td>
            <td class="px-4 py-2 border">{{ cm.updatedReplicas }}</td>
            <td class="px-4 py-2 border">
              {{ cm.containers ? Object.keys(cm.containers).length : 0 }}
            </td>
            <td class="px-4 py-2 border text-center">
              <button
                class="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600 transition"
                @click="selectDeployment(cm)"
              >
                View
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Deployment Detail Modal -->
    <div
      v-if="selectedDeployment"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center"
    >
      <div class="bg-white p-6 rounded-lg shadow-lg max-w-2xl w-full relative">
        <button
          class="absolute top-2 right-2 text-2xl font-bold leading-none text-gray-600 hover:text-gray-800"
          @click="selectedDeployment = null"
        >
          &times;
        </button>
        <h3 class="text-2xl font-bold mb-4">
          Deployment: {{ selectedDeployment.name }}
        </h3>
        <p class="mb-2">
          <strong>Namespace:</strong> {{ selectedDeployment.namespace }}
        </p>
        <div v-if="selectedDeployment.containers">
          <h4 class="text-xl font-semibold mb-2">Containers:</h4>
          <div
            v-for="(value, key) in selectedDeployment.containers"
            :key="key"
            class="mb-3"
          >
            <pre class="bg-gray-100 p-2 border rounded text-xs overflow-auto">{{
              value
            }}</pre>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { useK8sRealtime } from "../composables/useK8sRealtime.js";

const selectedDeployment = ref(null);
const errorMsg = ref(null);

// Realtime deployments comes from WebSocket
const { deployments } = useK8sRealtime();

// Optional: Load REST version first in case WebSocket is delayed
onMounted(async () => {
  try {
    const response = await axios.get("/api/deployments");
    // Only preload if WebSocket hasn't updated anything yet
    if (deployments.value.length === 0) {
      deployments.value = response.data;
    }
  } catch (error) {
    console.error("Error fetching deployments via REST:", error);
    errorMsg.value = "Failed to load deployments";
  }
});

const selectDeployment = (cm) => {
  selectedDeployment.value = cm;
};

const formatDate = (dateStr) => {
  if (!dateStr) return "N/A";
  const d = new Date(dateStr);
  return d.toLocaleString();
};
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
