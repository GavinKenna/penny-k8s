<template>
  <div class="flex h-full">
    <!-- Namespace Grid -->
    <div class="w-1/2 p-6 overflow-y-auto">
      <!-- Page Header -->
      <header class="mb-6">
        <h2 class="text-3xl font-bold text-blue-600">Namespaces Overview</h2>
        <p class="text-gray-600">
          A summary of your Namespaces, with live pod & deployment counts.
        </p>
      </header>

      <p v-if="errorMsg" class="text-red-500 mb-4">{{ errorMsg }}</p>

      <div class="grid gap-4 grid-cols-1 sm:grid-cols-2">
        <div
          v-for="ns in namespaces"
          :key="ns.name"
          class="bg-white p-4 rounded-xl shadow hover:shadow-lg transition cursor-pointer"
          @click="selectNamespace(ns)"
        >
          <h3 class="text-xl font-semibold text-gray-800 mb-1">
            {{ ns.name }}
          </h3>
          <p class="text-sm text-gray-500">
            <strong>Age:</strong> {{ formatDate(ns.creationTimestamp) }}
          </p>
          <p class="text-sm text-gray-500">
            <strong>Pods:</strong> {{ podCount[ns.name] || 0 }}
          </p>
          <p class="text-sm text-gray-500">
            <strong>Deployments:</strong> {{ deploymentCount[ns.name] || 0 }}
          </p>
        </div>
      </div>
    </div>

    <!-- Detail Pane -->
    <div class="w-1/2 p-6 overflow-y-auto border-l" v-if="selectedNamespace">
      <div class="flex justify-between items-start mb-4">
        <h3 class="text-2xl font-bold">{{ selectedNamespace.name }}</h3>
        <button
          @click="selectedNamespace = null"
          class="text-gray-500 hover:text-gray-700 text-2xl font-bold"
        >
          &times;
        </button>
      </div>

      <div class="space-y-4 text-sm text-gray-700">
        <p>
          <strong>Age:</strong>
          {{ formatDate(selectedNamespace.creationTimestamp) }}
        </p>
        <p>
          <strong>Pods:</strong> {{ podCount[selectedNamespace.name] || 0 }}
        </p>
        <p>
          <strong>Deployments:</strong>
          {{ deploymentCount[selectedNamespace.name] || 0 }}
        </p>

        <div>
          <h4 class="font-semibold mb-1">Annotations</h4>
          <pre class="bg-gray-100 p-3 rounded overflow-auto whitespace-pre-wrap"
            >{{ JSON.stringify(selectedNamespace.annotations, null, 2) }}
          </pre>
        </div>

        <div>
          <h4 class="font-semibold mb-1">Labels</h4>
          <pre class="bg-gray-100 p-3 rounded overflow-auto whitespace-pre-wrap"
            >{{ JSON.stringify(selectedNamespace.labels, null, 2) }}
          </pre>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import axios from "axios";
import { pennyWebsockets } from "../composables/pennyWebsockets.js";

const namespaces = ref([]);
const selectedNamespace = ref(null);
const errorMsg = ref(null);

const { pods, deployments, namespaces: wsNamespaces } = pennyWebsockets();

const loadInitial = async () => {
  try {
    const { data } = await axios.get("/api/namespaces");
    namespaces.value = data;
  } catch (e) {
    console.error("Failed to load namespaces", e);
    errorMsg.value = "Could not load namespaces";
  }
};
onMounted(loadInitial);

// Replace with WS data if available
if (wsNamespaces.value?.length) {
  namespaces.value = wsNamespaces.value;
}

const selectNamespace = (ns) => {
  selectedNamespace.value = ns;
};

const formatDate = (iso) => {
  if (!iso) return "N/A";
  return new Date(iso).toLocaleString();
};

const podCount = computed(() =>
  pods.value.reduce((map, p) => {
    const ns = p.namespace || "Unknown";
    map[ns] = (map[ns] || 0) + 1;
    return map;
  }, {}),
);

const deploymentCount = computed(() =>
  deployments.value.reduce((map, d) => {
    const ns = d.namespace || "Unknown";
    map[ns] = (map[ns] || 0) + 1;
    return map;
  }, {}),
);
</script>
