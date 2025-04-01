<script setup>
  import { inject } from 'vue'
  import { useRouter } from 'vue-router';
  import axios from 'axios';

  const globalState = inject('globalState');

  const router = useRouter();

  function logout() {
    let path = "http://localhost:8081/logout";
    if (globalState.isArtist.value) {
      path = "http://localhost:8081/artists/logout";
    }
    path += "?token=" + globalState.token.value;
    axios.post(path).then((response) => {
      globalState.token.value = null;
      globalState.userId.value = null;
      globalState.isArtist.value = false;
      localStorage.removeItem("token");
      localStorage.removeItem("isArtist");
      localStorage.removeItem("id");
      router.push("/login");
    }).catch((error) => {
      console.log(error);
      globalState.token.value = null;
      globalState.userId.value = null;
      globalState.isArtist.value = false;
      localStorage.removeItem("token");
      localStorage.removeItem("isArtist");
      localStorage.removeItem("id");
      router.push("/");
    });
    
  }

</script>

<template>
  <div class="header">
    <div id="contenedorLogo">
      <img src="../assets/Spootify_logo.png" alt="Spotify logo"/>
      <p id="titulo">Spootify</p>
    </div>
    <div class="side">
    <div class="header-box" v-if="!Number.isInteger(globalState.token.value)"><router-link to="/login">Mi perfil</router-link></div>
    <div class="header-box" v-else-if="globalState.isArtist.value === true"><router-link to="/artists/dashboard">Mi perfil</router-link></div>
    <div class="header-box" v-else-if="globalState.isArtist.value === false"><router-link to="/users/dashboard">Mi perfil</router-link></div>
    
    <div class="header-box" v-if="!Number.isInteger(globalState.token.value)"><router-link to="/login">Mis playlists</router-link></div>
    <div class="header-box" v-else><router-link to="/playlists">Mis playlists</router-link></div>
    
    <div class="header-box" v-if="Number.isInteger(globalState.token.value)"><a @click="logout">Log out</a></div>
    <div class="header-box" v-else><router-link to="/login">Log in</router-link></div>
    </div>
  </div>
</template>

<style scoped>
.header {
  font-family: 'Circular', sans-serif;
}

#titulo {
  color: white;
  align-self: center;
  font-size: x-large;
  font-weight: bold;
  margin-left: 10px;
}

#contenedorLogo {
  align-items: left;
  display: flex;
}

img {
    width: 4em;
    height: 4em;
    margin: 0.5em;
}

a {
  color: white;
  text-decoration: none;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

/* Style for exact active links (optional) */
a.router-link-exact-active {
  text-decoration: none;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  background-color: #2a2a2a;
  padding: 0;
  box-sizing: border-box;

}

.side {
  display: flex;
  width: 50%;
  background-color: transparent;
  height: 60px;
}

.header-box {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-weight: bold;
  background-color: transparent; /* Spotify green */
  cursor: pointer;
  transition: background-color 0.2s ease;
  /* No margin or padding horizontally to ensure boxes touch */
  padding: 0;
  border-right: 3px solid rgba(255, 255, 255, 1); /* subtle separator */
}

.header-box:last-child {
  border-right: none; /* Remove border from last item */
}

.header-box:hover {
  background-color: #1ed760; /* Lighter green on hover */
}
</style>