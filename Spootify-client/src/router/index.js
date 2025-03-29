import { createRouter, createWebHistory } from 'vue-router';
import Prueba from '../views/Prueba.vue';
import Login from '../views/Login.vue';
import Main from '../views/Main.vue';
import ArtistDashboard from '../views/ArtistDashboard.vue';
import Album from '../views/Album.vue';
import CreateAlbum from '@/components/CreateAlbum.vue';
import ArtistDetail from '../views/ArtistDetail.vue';

const routes = [
  { path: '/', component: Main },
  { path: '/prueba', component: Prueba },
  { path: '/login', component: Login },
  { path: '/artists/dashboard', component: ArtistDashboard },
  { path : '/crearAlbum', component: CreateAlbum },
  { path : '/albums/:id', component: Album },
  { path: '/artists/:id', component: ArtistDetail }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
