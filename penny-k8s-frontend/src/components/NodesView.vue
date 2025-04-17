<template>
  <div>
    <header class="mb-8 text-center">
      <h2 class="text-3xl font-bold text-blue-600">Nodes Overview</h2>
    </header>

    <div class="grid gap-6 grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
      <div
        v-for="node in nodes"
        :key="node.name"
        class="bg-white p-6 rounded-2xl shadow-md"
      >
        <h3 class="text-xl font-semibold text-gray-800 mb-2">
          {{ node.name }}
        </h3>
        <p><strong>Role:</strong> {{ node.role }}</p>
        <p>
          <strong>Status:</strong>
          <span
            :class="node.status === 'Ready' ? 'text-green-600' : 'text-red-600'"
          >
            {{ node.status }}
          </span>
        </p>
        <p><strong>CPU:</strong> {{ node.cpu }}</p>
        <p><strong>Memory:</strong> {{ node.memory }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { useK8sRealtime } from "../composables/useK8sRealtime.js";

const selectedNode = ref(null);
const errorMsg = ref(null);

// Realtime Nodes comes from WebSocket
const { nodes } = useK8sRealtime();

// Optional: Load REST version first in case WebSocket is delayed
onMounted(async () => {
  try {
    const response = await axios.get("/api/nodes");
    // Only preload if WebSocket hasn't updated anything yet
    if (nodes.value.length === 0) {
      nodes.value = response.data;
    }
  } catch (error) {
    console.error("Error fetching Nodes via REST:", error);
    errorMsg.value = "Failed to load Nodes";
  }
});

const selectNode = (cm) => {
  selectedNode.value = cm;
};

const formatDate = (dateStr) => {
  if (!dateStr) return "N/A";
  const d = new Date(dateStr);
  return d.toLocaleString();
};
</script>
