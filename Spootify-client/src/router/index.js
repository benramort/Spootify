import { createRouter, createWebHistory } from 'vue-router';
import Prueba from '../views/Prueba.vue';
import Login from '../views/Login.vue';
import Main from '../views/Main.vue';
import ArtistDashboard from '../views/ArtistDashboard.vue';
import CreateAlbum from '@/components/CreateAlbum.vue';

const routes = [
  { path: '/', component: Main },
  { path: '/prueba', component: Prueba },
  { path: '/login', component: Login },
  { path: '/artist/dashboard', component: ArtistDashboard },
  { path : '/crearAlbum', component: CreateAlbum }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
