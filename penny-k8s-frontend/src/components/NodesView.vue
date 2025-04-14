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
        <h3 class="text-xl font-semibold text-gray-800 mb-2">{{ node.name }}</h3>
        <p><strong>Role:</strong> {{ node.role }}</p>
        <p><strong>Status:</strong>
          <span :class="node.status === 'Ready' ? 'text-green-600' : 'text-red-600'">
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
import { ref, onMounted } from 'vue'
import { fetchNodes } from '../services/penny-service.js'

const nodes = ref([])

onMounted(async () => {
  nodes.value = await fetchNodes()
})
</script>
