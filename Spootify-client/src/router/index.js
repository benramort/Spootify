import { createRouter, createWebHistory } from 'vue-router';
import App2 from '../views/App2.vue';

const routes = [
  { path: '/login', component: App2 }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
