<template>
  <div class="flex h-screen w-full overflow-hidden bg-gray-50">
    <!-- Sidebar: Fixed width at the far left -->
    <aside class="w-56 bg-white border-r border-gray-200 p-10 flex-shrink-0">
      <img src="../assets/pennyK8s.png" alt="Penny K8s" class="h-16 sm:h-20 lg:h-24 mx-auto mb-4" />
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
import { ref } from "vue";
import PodsView from "./PodsView.vue";
import NodesView from "./NodesView.vue";
import ConfigMapsView from "./ConfigMapsView.vue";
import DeploymentsView from "./DeploymentsView.vue";
import NamespacesView from "./NamespacesView.vue";
import ServicesView from "./ServicesView.vue";

const views = ["Nodes", "Namespaces", "Pods", "ConfigMaps", "Deployments", "Services"];
const selectedView = ref("Pods");

const getComponentForView = (view) => {
  switch (view) {
    case "Namespaces":
      return NamespacesView;
    case "Nodes":
      return NodesView;
    case "ConfigMaps":
      return ConfigMapsView;
    case "Deployments":
      return DeploymentsView;
      case "Services":
      return ServicesView;
    default:
      return PodsView;
  }
};

const buttonClass = (view) => {
  return [
    "w-full text-left px-3 py-2 rounded hover:bg-blue-100 transition",
    selectedView.value === view
      ? "bg-blue-500 text-white font-semibold"
      : "text-gray-700",
  ];
};
</script>

<style scoped>
/* No extra width or container constraints are added here */
</style>
