<!-- Para lanzar: 
 (npm install)
 npm run dev-->

<script setup> //Para no poner export default
// import HelloWorld from './components/HelloWorld.vue'
// import TheWelcome from './components/TheWelcome.vue'
// import Prueba from './components/Prueba.vue'
import Headervue from './components/Header.vue'

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

  </footer>
</template>

<style>
/* Global styles (not scoped) to reset margins */
html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
}

/* Application container */
#app {
  width: 100%;
  min-height: 100vh;

}

body {
  overflow: hidden;
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
}

main {
  width: 100%;
  margin-top: 0;  
  padding: 20px;
  box-sizing: border-box;
  /* border: 1px solid magenta; */
}

footer {
  width: 100%;
  padding: 10px 0;
}
</style>
