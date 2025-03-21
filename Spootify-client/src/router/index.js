import { createRouter, createWebHistory } from 'vue-router';
import Prueba from '../views/Prueba.vue';
import Login from '../views/Login.vue';

const routes = [
  { path: '/prueba', component: Prueba },
  { path: '/login', component: Login }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
