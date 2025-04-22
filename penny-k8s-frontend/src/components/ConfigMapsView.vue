<template>
  <div class="flex h-[calc(100vh-4rem)]">
    <!-- ConfigMaps List -->
    <div class="w-1/2 p-4 overflow-y-auto border-r border-gray-200">
      <header class="mb-6 text-center">
        <h2 class="text-3xl font-bold text-blue-600">ConfigMaps</h2>
        <p class="text-gray-600">A summary of your ConfigMaps.</p>
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
              <th class="px-4 py-2 border"># of Keys</th>
              <th class="px-4 py-2 border">Age</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="cm in configMaps"
              :key="cm.name"
              class="border-b hover:bg-gray-50 cursor-pointer"
              :class="{ 'bg-blue-50': selectedConfigMap?.name === cm.name }"
              @click="selectConfigMap(cm)"
            >
              <td class="px-4 py-2 border">{{ cm.name }}</td>
              <td class="px-4 py-2 border">{{ cm.namespace }}</td>
              <td class="px-4 py-2 border">
                {{ cm.data ? Object.keys(cm.data).length : 0 }}
              </td>
              <td class="px-4 py-2 border">
                {{ formatDate(cm.creationTimestamp) }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ConfigMap Details Panel -->
    <div class="w-1/2 p-6 overflow-y-auto">
      <div v-if="selectedConfigMap">
        <h3 class="text-2xl font-bold mb-4 text-blue-700">
          ConfigMap: {{ selectedConfigMap.name }}
        </h3>
        <p class="mb-2">
          <strong>Namespace:</strong> {{ selectedConfigMap.namespace }}
        </p>
        <p class="mb-4">
          <strong>Created:</strong>
          {{ formatDate(selectedConfigMap.creationTimestamp) }}
        </p>

        <div v-if="selectedConfigMap.data">
          <h4 class="text-xl font-semibold mb-2">Data:</h4>
          <div
            v-for="(value, key) in selectedConfigMap.data"
            :key="key"
            class="mb-4"
          >
            <p class="font-medium">{{ key }}:</p>
            <pre
              class="bg-gray-100 p-2 border rounded text-sm overflow-auto whitespace-pre-wrap"
              >{{ value }}
            </pre>
          </div>
        </div>
      </div>
      <div v-else class="text-gray-500 text-center mt-10">
        <p class="text-xl">Select a ConfigMap to view details</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { pennyWebsockets } from "../composables/pennyWebsockets.js";

const { configMaps } = pennyWebsockets();
const selectedConfigMap = ref(null);
const errorMsg = ref(null);

function selectConfigMap(cm) {
  selectedConfigMap.value = cm;
}

function formatDate(dateStr) {
  if (!dateStr) return "N/A";
  return new Date(dateStr).toLocaleString();
}

// Initial load
onMounted(async () => {
  try {
    const { data } = await axios.get("/api/configmaps");
    configMaps.value = data;
  } catch (err) {
    console.error(err);
    errorMsg.value = "Failed to load existing ConfigMaps";
  }
});
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
