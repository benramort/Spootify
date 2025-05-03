<!-- Para lanzar: 
 (npm install)
 npm run dev-->

<script setup> //Para no poner export default
// import HelloWorld from './components/HelloWorld.vue'
// import TheWelcome from './components/TheWelcome.vue'
// import Prueba from './components/Prueba.vue'
import Headervue from './components/Header.vue'
import Reproductor from './components/Reproductor.vue';
import { ref, provide } from 'vue';

const reproductor = ref(null);

provide('reproductor', {
  playSong: (song) => {
    if (reproductor.value) {
      reproductor.value.selectSong(song);
    }
  }
})

</script>

<template>
  <header>
    <!-- <router-link to="/login">Login</router-link> -->
    <Headervue />
  </header>
  <main>
    <router-view />
  </main>
  <footer>
    <Reproductor ref="reproductor" />
  </footer>
</template>

<style>
@font-face {
  font-family: 'Circular';
  src: url('./assets/circular-black.woff') format('woff');
  font-weight: 900;
  font-style: normal;
  font-display: swap;
}

/* Global styles (not scoped) to reset margins */
html, body {
  /* font-family: 'Circular', sans-serif; */
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  overflow: hidden; /* Prevent double scrollbars */
}

/* Application container */
#app {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

h1, h2, h3, h4, h5, h6 {
  font-family: 'Circular', sans-serif;
}
</style>

<style scoped>
header {
  width: 100%;
  margin: 0;
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 100;
  flex-shrink: 0;
}

main {
  flex: 1;
  width: 100%;
  overflow-y: auto; /* Enable scrolling for main content */
  overflow-x: hidden;
  padding: 0px;
  box-sizing: border-box;
  /* Add a subtle scrollbar style */
  scrollbar-width: thin;
  scrollbar-color: #888 #f1f1f1;
}

/* Webkit scrollbar styling */
main::-webkit-scrollbar {
  width: 8px;
}

main::-webkit-scrollbar-track {
  background: #f1f1f1;
}

main::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 4px;
}

main::-webkit-scrollbar-thumb:hover {
  background: #555;
}

footer {
  width: 100%;
  padding: 10px 0;
  flex-shrink: 0;
  position: sticky;
  bottom: 0;
  z-index: 100;
  background-color: #fff; /* Add a background color to prevent content showing through */
}
</style>