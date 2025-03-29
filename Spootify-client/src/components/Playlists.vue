<script setup>
import router from "@/router";
import axios from "axios";
import { onMounted, inject, ref } from "vue";
import { useRoute } from "vue-router";

const globalState = inject("globalState");

const props = defineProps({
    path: {
        type: String,
        default: "playlists"
    }
});

const playlists = ref([]);

onMounted(() => {
    let path = "http://localhost:8081/" + props.path + "?";
    path = path + "token=" + globalState.token.value;
    console.log(path);
    axios.get(path).then((response) => {
        playlists.value = response.data;
        console.log(playlists.value);
        console.log(playlists.value.length);
    });
});
</script>

<template>
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
    gap: 20px; /* Espacio entre las playlists */
    padding: 20px;
}

.playlist {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%; /* Ocupa todo el espacio disponible en la celda */
    height: 150px; /* Altura fija */
    background-color: red;
    border: none;
    border-radius: 10px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.playlist:hover {
    background-color: blue;
}

.playlist p {
    color: white;
    font-size: 16px;
    font-weight: bold;
    text-align: center;
}
</style>