<script setup>
    import { inject, onMounted } from 'vue';
import Prueba from '../components/Songs.vue';
    import Login from '../views/Login.vue'
import router from '@/router';

    const globalState = inject('globalState');

    onMounted(() => {
        if(isNaN(globalState.token.value)) {
            return;
        } else {
            if(globalState.isArtist.value) {
                router.push('/artists/dashboard');
            } else {
                router.push('/users/dashboard')
            }
        }

    });

    function goToRegister() {
  router.push('/login'); 
}
</script>

<template>
    <div class="hero-container">
    <div class="overlay">
      <h1>Register before they shut us down</h1>
      <button class="register-button" @click="goToRegister">Sing up/Login</button>
    </div>
  </div>

</template>


<style scoped>
    .hero-container {
        position: relative;
        width: 100%;
        height: 100vh; 
        background-image: url('https://www.shutterstock.com/image-photo/band-on-stage-music-show-600nw-645446737.jpg'); /* Cambia 'your-image.jpg' por la ruta de tu imagen */
        background-size: cover;
        background-position: center;
        margin:0;
        padding:0;
    }

    .overlay {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5); /* Capa negra semitransparente */
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        text-align: center;
    }

    .overlay h1 {
        color: white;
        font-size: 2.5rem;
        margin-bottom: 20px;
    }

    .register-button {
        background-color: #1ed760; /* Verde estilo Spotify */
        color: white;
        border: none;
        padding: 10px 20px;
        font-size: 1rem;
        font-weight: bold;
        border-radius: 5px;
        cursor: pointer;
        transition: background-color 0.3s ease;
    }

    .register-button:hover {
        background-color: #1db954; /* Verde más oscuro al pasar el cursor */
    }
    .columns {
        display: flex;
        justify-content: space-between;
        border: 5px solid blue;
    }

    .column {
        border: 2px solid red;
        width: 50%;
    }

    html, body {
        margin: 0;
        padding: 0;
        height: 100%;
        overflow: hidden; /* Evita barras de desplazamiento innecesarias */
    }
</style>