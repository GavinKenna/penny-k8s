<template>
  <div class="flex h-[calc(100vh-4rem)]">
    <!-- RoleBindings List -->
    <div class="w-1/2 p-4 overflow-y-auto border-r border-gray-200">
      <header class="mb-6 text-center">
        <h2 class="text-3xl font-bold text-blue-600">RoleBindings</h2>
        <p class="text-gray-600">A summary of your RoleBindings.</p>
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
              v-for="cm in roleBindings"
              :key="cm.name"
              class="border-b hover:bg-gray-50 cursor-pointer"
              :class="{ 'bg-blue-50': selectedRoleBinding?.name === cm.name }"
              @click="selectRoleBinding(cm)"
            >
              <td class="px-4 py-2 border">{{ cm.name }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- RoleBinding Details Panel -->
    <div class="w-1/2 p-6 overflow-y-auto">
      <div v-if="selectedRoleBinding">
        <h3 class="text-2xl font-bold mb-4 text-blue-700">
          RoleBinding: {{ selectedRoleBinding.name }}
        </h3>

        <div v-if="selectedRoleBinding.subjects">
          <h4 class="text-xl font-semibold mb-2">Subjects:</h4>
          <div
            v-for="(value, key) in selectedRoleBinding.subjects"
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
        <p class="text-xl">Select a RoleBinding to view details</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { pennyWebsockets } from "../composables/pennyWebsockets.js";

const { roleBindings } = pennyWebsockets();
const selectedRoleBinding = ref(null);
const errorMsg = ref(null);

function selectRoleBinding(cm) {
  selectedRoleBinding.value = cm;
}

function formatDate(dateStr) {
  if (!dateStr) return "N/A";
  return new Date(dateStr).toLocaleString();
}

// Initial load
onMounted(async () => {
  try {
    const { data } = await axios.get("/api/roleBindings");
    roleBindings.value = data;
  } catch (err) {
    console.error(err);
    errorMsg.value = "Failed to load existing RoleBindings";
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
