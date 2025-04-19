<template>
  <div>
    <header class="mb-8 text-center">
      <h2 class="text-3xl font-bold text-blue-600">Namespaces Overview</h2>
    </header>

    <div class="grid gap-6 grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
      <div
        v-for="namespace in namespaces"
        :key="namespace.name"
        class="bg-white p-6 rounded-2xl shadow-md"
      >
        <h3 class="text-xl font-semibold text-gray-800 mb-2">
          {{ namespace.name }}
        </h3>
        <p><strong>Role:</strong> {{ namespace.role }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { useK8sRealtime } from "../composables/useK8sRealtime.js";

const selectedNamespace = ref(null);
const errorMsg = ref(null);

// Realtime Namespaces comes from WebSocket
const { namespaces } = useK8sRealtime();

// Optional: Load REST version first in case WebSocket is delayed
onMounted(async () => {
  try {
    const response = await axios.get("/api/namespaces");
    // Only preload if WebSocket hasn't updated anything yet
    if (namespaces.value.length === 0) {
      namespaces.value = response.data;
    }
  } catch (error) {
    console.error("Error fetching Namespaces via REST:", error);
    errorMsg.value = "Failed to load Namespaces";
  }
});

const selectNamespace = (cm) => {
  selectedNamespace.value = cm;
};

const formatDate = (dateStr) => {
  if (!dateStr) return "N/A";
  const d = new Date(dateStr);
  return d.toLocaleString();
};
</script>
