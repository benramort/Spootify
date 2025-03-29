import { createRouter, createWebHistory } from 'vue-router';
import Prueba from '../views/Prueba.vue';
import Login from '../views/Login.vue';
import Main from '../views/Main.vue';
import ArtistDashboard from '../views/ArtistDashboard.vue';
import Album from '../views/Album.vue';
import CreateAlbum from '@/components/CreateAlbum.vue';
import CreatePlayList from '@/components/CreatePlayList.vue';
import Playlist from '@/components/Playlists.vue';

const routes = [
  { path: '/', component: Main },
  { path: '/prueba', component: Prueba },
  { path: '/login', component: Login },
  { path: '/artist/dashboard', component: ArtistDashboard },
  { path : '/crearAlbum', component: CreateAlbum },
  { path : '/albums/:id', component: Album },
  { path : '/crearPlaylist', component: CreatePlayList },
  { path: '/playlists/:id', component: Playlist}
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
