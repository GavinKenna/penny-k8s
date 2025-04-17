import "./assets/main.css";

import { createApp } from "vue";
import App from "./App.vue";
import * as lucide from "lucide-vue-next";

const app = createApp(App);
Object.entries(lucide).forEach(([name, component]) => {
  app.component(name, component);
});
app.mount("#app");
