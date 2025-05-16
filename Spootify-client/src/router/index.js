import { createRouter, createWebHistory } from 'vue-router';
import Prueba from '../views/Prueba.vue';
import Login from '../views/Login.vue';
import Main from '../views/Main.vue';
import ArtistDashboard from '../views/ArtistDashboard.vue';
import Album from '../views/Album.vue';
import CreateAlbum from '@/components/CreateAlbum.vue';
import CreatePlayList from '@/components/CreatePlayList.vue';
import Playlists from '@/views/Playlists.vue';
import ArtistDetail from '../views/ArtistDetail.vue';
import UserDashboard from '../views/UserDashboard.vue';
import Playlist from '@/views/Playlist.vue';
import Search from '@/views/Search.vue';
import Reproductor from '@/components/Reproductor.vue';
import MostPopular from '@/components/MostPopular.vue';

const routes = [
  { path: '/', component: Main },
  { path: '/prueba', component: Prueba },
  { path: '/login', component: Login },
  { path: '/artists/dashboard', component: ArtistDashboard },
  { path : '/crearAlbum', component: CreateAlbum },
  { path : '/albums/:id', component: Album },
  { path: '/artists/:id', component: ArtistDetail },
  { path: '/users/dashboard', component: UserDashboard},
  { path : '/crearPlaylist', component: CreatePlayList },
  { path: '/playlists', component: Playlists},
  { path: '/playlists/:id', component: Playlist },
  { path: '/artists/:id', component: ArtistDetail },
  {path: '/search',component: Search},
  {path: '/stream', component: Reproductor},
  {path: '/mostPopular', component: MostPopular}
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
