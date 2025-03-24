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
      console.log(response);
    }).catch((error) => {
      console.log(error);
    });
    globalState.token.value = 0;
    globalState.userId.value = 0;
    globalState.isArtist.value = false;
    localStorage.removeItem("token");
    localStorage.removeItem("isArtist");
    localStorage.removeItem("id");
    router.push("/");
    console.log("Logged out");
  }

</script>

<template>
  <div class="header">
    <div id="contenedorLogo">
      <img src="../assets/Spootify_logo.png" alt="Spotify logo"/>
      <p id="titulo">Spootify</p>
    </div>
    <div class="side">
    <div class="header-box"><router-link to="/login"></router-link></div>
    <div class="header-box"><router-link to="/artist/dashboard">Mi perfil</router-link></div>
    <div class="header-box"><router-link to="/crearAlbum">Home</router-link></div>
    <div class="header-box"><router-link to="/login">Home</router-link></div>
    <div class="header-box" v-if="globalState.token.value == 0"><router-link to="/login">Log in</router-link></div>
    <div class="header-box" v-if="globalState.token.value != 0"><p @click="logout()">Log out</p></div>
    </div>
  </div>
</template>

<style scoped>
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
  text-decoration: underline;
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
  width: 60%;
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