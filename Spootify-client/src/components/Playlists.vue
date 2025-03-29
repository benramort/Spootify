<script setup>
import router from "@/router";
import axios from "axios";
import { onMounted, inject, ref } from "vue";
import { useRoute } from "vue-router";
import CreatePlayList from "./CreatePlayList.vue";

const globalState = inject("globalState");
const showModalAlbum = ref(false);
const props = defineProps({
    path: {
        type: String,
        default: "playlists"
    }
});

const playlists = ref([]);

onMounted(() => {
    let path = "http://localhost:8081/playlists";
    path = path + "?token=" + globalState.token.value;
    console.log(path);
    axios.get(path).then((response) => {
        playlists.value = response.data;
        console.log(playlists.value);
        console.log(playlists.value.length);
    });
});
</script>

<template>
    <div>
        <CreatePlayList :isOpen="showModalAlbum" @close="showModalAlbum = false" />
        <button @click="showModalAlbum = true">
            <i class="fa-solid fa-plus inside-button"></i>
            <p class="inside-button">Nueva playlist</p>
        </button>
    </div>
    <div class="grid-container">
        <button
            class="playlist"
            v-for="playlist in playlists"
            :key="playlist.id"
            @click="router.push('/playlists/' + playlist.id)"
        >
            <p>{{ playlist.name }}</p>
        </button>
    </div>
</template>

<style scoped>
.grid-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); /* Ajusta el tamaño mínimo y máximo */
    gap: 20px; /* Espacio reducido entre las playlists */
    padding: 40px;
    margin-left: 16%;
    margin-right: 16%;
    max-width: 100%; /* Define un ancho máximo relativo para que haya el mismo espacio a los lados */
}

.playlist {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 200px; /* Ancho fijo para que sea cuadrado */
    height: 200px; /* Altura fija para que sea cuadrado */
    background-color: red;
    border: none;
    border-radius: 0px; /* Bordes cuadrados */
    cursor: pointer;
    transition: background-color 0.3s ease;
    text-align: center; /* Centra el texto */
    word-wrap: break-word; /* Permite que el texto se ajuste a varias líneas */
    overflow-wrap: break-word; /* Ajusta el texto si es muy largo */
}

.playlist:hover {
    background-color: blue;
}

.playlist p {
    color: white;
    font-size: 14px; /* Tamaño de fuente ajustado */
    font-weight: bold;
    margin: 0; /* Elimina márgenes adicionales */
    padding: 10px; /* Espaciado interno para el texto */
    overflow: hidden; /* Evita que el texto desborde */
    text-overflow: ellipsis; /* Agrega puntos suspensivos si el texto es demasiado largo */
}
</style>