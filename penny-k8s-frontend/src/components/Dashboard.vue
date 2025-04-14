<template>
  <div class="flex h-screen w-full overflow-hidden bg-gray-50">
    <!-- Sidebar: Fixed width at the far left -->
    <aside class="w-56 bg-white border-r border-gray-200 p-10 flex-shrink-0">
      <h1 class="text-xl font-bold text-blue-600 mb-6">Penny K8s Dashboard</h1>
      <nav class="space-y-2">
        <button
            v-for="view in views"
            :key="view"
            @click="selectedView = view"
            :class="buttonClass(view)"
        >
          {{ view }}
        </button>
      </nav>
    </aside>

    <!-- Main Content: Fill the remaining space -->
    <main class="flex-1 overflow-y-auto p-6">
      <component :is="getComponentForView(selectedView)" />
    </main>
  </div>
</template>

<script setup>
import {ref} from 'vue'
import PodsView from './PodsView.vue'
import NodesView from './NodesView.vue'
import ConfigMapsView from './ConfigMapsView.vue'

const views = ['Pods', 'Nodes', 'ConfigMaps']
const selectedView = ref('Pods')

const getComponentForView = (view) => {
  switch (view) {
    case 'Nodes':
      return NodesView
    case 'ConfigMaps':
      return ConfigMapsView
    default:
      return PodsView
  }
}

const buttonClass = (view) => {
  return [
    'w-full text-left px-3 py-2 rounded hover:bg-blue-100 transition',
    selectedView.value === view ? 'bg-blue-500 text-white font-semibold' : 'text-gray-700'
  ]
}
</script>

<style scoped>
/* No extra width or container constraints are added here */
</style>
