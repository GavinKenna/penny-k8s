<template>
  <div>
    <!-- Header -->
    <header class="mb-8 text-center">
      <h2 class="text-3xl font-bold text-blue-600">Pods Overview</h2>
    </header>

    <!-- Search Bar -->
    <div class="mb-6 flex justify-center">
      <input
        type="text"
        v-model="searchTerm"
        placeholder="Search Pods..."
        class="p-2 border rounded mb-4 w-full sm:w-1/2 md:w-1/3"
      />
    </div>

    <!-- Tabs for Node Types -->
    <div class="tabs flex space-x-4 mb-6 justify-center">
      <button
        class="tab py-2 px-4 text-lg"
        @click="filterNodes('all')"
        :class="{ 'text-blue-500 font-semibold': nodeFilter === 'all' }"
      >
        All Nodes
      </button>
      <button
        class="tab py-2 px-4 text-lg"
        @click="filterNodes('master')"
        :class="{ 'text-blue-500 font-semibold': nodeFilter === 'master' }"
      >
        Master
      </button>
      <button
        class="tab py-2 px-4 text-lg"
        @click="filterNodes('worker')"
        :class="{ 'text-blue-500 font-semibold': nodeFilter === 'worker' }"
      >
        Worker
      </button>
    </div>

    <!-- Nodes and Pods -->
    <section
      v-for="(pods, nodeName) in filteredPodsPerNode"
      :key="nodeName"
      class="bg-white p-6 rounded-2xl shadow-md mb-6"
    >
      <details open>
        <summary
          class="flex items-center text-xl font-semibold text-blue-600 mb-4 cursor-pointer"
        >
          <svg
            class="w-6 h-6 mr-2 text-blue-500"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M12 20v-6m0 0V4m0 10h6m-6 0H6"
            />
          </svg>
          {{ nodeName }}
          <svg
            v-if="!isNodeExpanded(nodeName)"
            class="ml-auto w-4 h-4 text-blue-500"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M19 9l-7 7-7-7"
            />
          </svg>
          <svg
            v-else
            class="ml-auto w-4 h-4 text-blue-500"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M5 15l7-7 7 7"
            />
          </svg>
        </summary>

        <ul>
          <li
            v-for="pod in pods"
            :key="pod.name"
            class="border-b last:border-none px-2 py-3"
          >
            <details>
              <summary class="flex justify-between items-center cursor-pointer">
                <div>
                  <span class="font-medium text-gray-800">{{ pod.name }}</span>
                  <small class="ml-2 text-gray-500"
                    >({{ pod.namespace }})</small
                  >
                </div>
                <span
                  :class="
                    pod.status === 'Running'
                      ? 'text-green-500 font-semibold'
                      : 'text-red-500 font-semibold'
                  "
                >
                  {{ pod.status }}
                </span>
              </summary>

              <div class="mt-3 ml-4 text-sm text-gray-700">
                <p><strong>Node:</strong> {{ pod.nodeName || "Unknown" }}</p>
                <p><strong>Pod IP:</strong> {{ pod.podIP || "N/A" }}</p>
                <p>
                  <strong>Start Time:</strong> {{ formatDate(pod.startTime) }}
                </p>
                <p><strong>Containers:</strong></p>
                <ul class="list-disc ml-6">
                  <li v-for="container in pod.containers" :key="container.name">
                    {{ container.name }} ({{ container.image }})
                  </li>
                </ul>
                <button
                  class="mt-3 px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
                  @click="fetchPodLogs(pod)"
                >
                  View Logs
                </button>
                <pre
                  v-if="selectedPodLogs[pod.name]"
                  class="mt-2 p-2 bg-gray-100 border rounded text-xs overflow-x-auto"
                  >{{ selectedPodLogs[pod.name] }}
                </pre>
              </div>
            </details>
          </li>
        </ul>
      </details>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import axios from "axios";
import { pennyWebsockets } from "../composables/pennyWebsockets.js";
import { fetchNodes, fetchPods, fetchLogs } from "../services/penny-service.js";

const selectedPodLogs = ref({});
const searchTerm = ref("");
const nodeFilter = ref("all");

const fetchPodLogs = async (pod) => {
  if (selectedPodLogs.value[pod.name]) return;
  const logs = await fetchLogs(pod.namespace, pod.name);
  selectedPodLogs.value[pod.name] = logs;
};

const formatDate = (dateStr) => {
  const d = new Date(dateStr);
  return d.toLocaleString();
};

const nodes = ref([]);
const selectedPod = ref(null);
const errorMsg = ref(null);

// Realtime Pods comes from WebSocket
const { pods } = pennyWebsockets();

// Optional: Load REST version first in case WebSocket is delayed
onMounted(async () => {
  try {
    const response = await axios.get("/api/pods");
    // Only preload if WebSocket hasn't updated anything yet
    if (pods.value.length === 0) {
      pods.value = response.data;
    }
    const responseNode = await axios.get("/api/nodes");
    // Only preload if WebSocket hasn't updated anything yet
    if (nodes.value.length === 0) {
      nodes.value = responseNode.data;
    }
  } catch (error) {
    console.error("Error fetching Pods via REST:", error);
    errorMsg.value = "Failed to load Pods";
  }
});

const selectPod = (cm) => {
  selectedPod.value = cm;
};

const podsPerNode = computed(() => {
  const map = {};
  for (const pod of pods.value) {
    const nodeName = pod.nodeName || "Unknown";
    if (!map[nodeName]) map[nodeName] = [];
    map[nodeName].push(pod);
  }
  return map;
});

const filteredPodsPerNode = computed(() => {
  let filtered = podsPerNode.value;

  if (nodeFilter.value !== "all") {
    filtered = Object.fromEntries(
      Object.entries(filtered).filter(([nodeName]) =>
        nodeName.toLowerCase().includes(nodeFilter.value),
      ),
    );
  }

  if (searchTerm.value) {
    filtered = Object.fromEntries(
      Object.entries(filtered).map(([nodeName, pods]) => [
        nodeName,
        pods.filter((pod) =>
          pod.name.toLowerCase().includes(searchTerm.value.toLowerCase()),
        ),
      ]),
    );
  }

  return filtered;
});

const filterNodes = (filter) => {
  nodeFilter.value = filter;
};

const isNodeExpanded = (nodeName) => {
  return podsPerNode.value[nodeName].length > 0;
};
</script>

<style scoped>
summary::-webkit-details-marker {
  display: none;
}

.tabs button {
  transition: background-color 0.3s ease;
}

.tabs button:hover {
  background-color: #f0f0f0;
}

.tab {
  cursor: pointer;
}
</style>
