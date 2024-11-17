import { createRouter, createWebHistory } from 'vue-router'
import Scenario from "../components/Feature.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Scenario
    }
  ]
})

export default router
