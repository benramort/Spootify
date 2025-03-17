<!-- Para lanzar: 
 (npm install)
 npm run dev-->

<script setup> //Para no poner export default
// import HelloWorld from './components/HelloWorld.vue'
// import TheWelcome from './components/TheWelcome.vue'
// import Prueba from './components/Prueba.vue'
</script>

<template>
  <header>
    <router-link to="/login">Login</router-link>
  </header>
  <main>
    <router-view />
  </main>
</template>

<style scoped>

</style>
