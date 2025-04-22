<template>
  <div class="flex h-[calc(100vh-4rem)]">
    <!-- Roles List -->
    <div class="w-1/2 p-4 overflow-y-auto border-r border-gray-200">
      <header class="mb-6 text-center">
        <h2 class="text-3xl font-bold text-blue-600">Roles</h2>
        <p class="text-gray-600">A summary of your Roles.</p>
      </header>

      <div v-if="errorMsg" class="mb-4 text-red-600 text-center">
        {{ errorMsg }}
      </div>

      <div class="overflow-x-auto">
        <table class="min-w-full border-collapse">
          <thead>
          <tr class="bg-gray-100">
            <th class="px-4 py-2 border">Name</th>
          </tr>
          </thead>
          <tbody>
          <tr
              v-for="cm in roles"
              :key="cm.name"
              class="border-b hover:bg-gray-50 cursor-pointer"
              :class="{ 'bg-blue-50': selectedRole?.name === cm.name }"
              @click="selectRole(cm)"
          >
            <td class="px-4 py-2 border">{{ cm.name }}</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Role Details Panel -->
    <div class="w-1/2 p-6 overflow-y-auto">
      <div v-if="selectedRole">
        <h3 class="text-2xl font-bold mb-4 text-blue-700">
          Role: {{ selectedRole.name }}
        </h3>

        <div v-if="selectedRole.rules">
          <h4 class="text-xl font-semibold mb-2">Rules:</h4>
          <div
              v-for="(value, key) in selectedRole.rules"
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
        <p class="text-xl">Select a Role to view details</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { pennyWebsockets } from "../composables/pennyWebsockets.js";

const { roles } = pennyWebsockets();
const selectedRole = ref(null);
const errorMsg = ref(null);

function selectRole(cm) {
  selectedRole.value = cm;
}

function formatDate(dateStr) {
  if (!dateStr) return "N/A";
  return new Date(dateStr).toLocaleString();
}

// Initial load
onMounted(async () => {
  try {
    const {data} = await axios.get("/api/roles");
    roles.value = data;
  } catch (err) {
    console.error(err);
    errorMsg.value = "Failed to load existing Roles";
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
