<script setup>
import { onMounted, ref, inject } from "vue";
import { useRoute } from "vue-router";
import Songs from "../components/Songs.vue";
import axios from "axios";

const globalState = inject("globalState");

const route = useRoute();

const playlist = ref({
    name: "",
    songs: [],
});

const reproductor = inject("reproductor");

function play(song) {
    reproductor.selectSong(song);
}

onMounted(() => {
    fetchPlaylistDetails();
});

function fetchPlaylistDetails() {
    const playlistId = route.params.id; // Obtiene el ID de la playlist desde la ruta
    const token = globalState.token.value; // Obtiene el token del estado global

    if (!token) {
        console.error("Token no disponible. Asegúrate de haber iniciado sesión.");
        playlist.value = null;
        return;
    }

    const path = `http://localhost:8081/playlists/${playlistId}?token=${token}`; // Incluye el token en la URL

    axios
        .get(path)
        .then((response) => {
            console.log(response.data);
            playlist.value = response.data;
        })
        .catch((error) => {
            console.error("Error al cargar la playlist:", error);
            playlist.value = null; // Maneja el error asignando null
        });
}

function playAll() {
    try {
        let albumWithoutFirst = playlist.value.songs.slice(1, playlist.value.songs.length);
        console.log(albumWithoutFirst);
        reproductor.addToQueue(albumWithoutFirst);
        reproductor.selectSong(playlist.value.songs[0]);
    } catch (error) {
        console.log(error);
    }
}
</script>

<template>
    <div v-if="playlist.name">
        <div class="columns">
            <div class="columnLeft">
                <h2>{{ playlist.name }}</h2>
                <button>
                    <i class="fa-solid fa-circle-play" @click="playAll()"></i>
                </button>
            </div>
            <div class="columnRight">
                <div class="songs">
                    <!-- Verifica si la playlist tiene canciones -->
                    <Songs v-if="playlist.songs.length > 0" :songs="playlist.songs" />
                    <!-- Mensaje si no hay canciones -->
                    <div v-if="playlist.songs.length === 0">
                        <p>Esta playlist no tiene canciones.</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div v-else>
        <p>No se pudo cargar la playlist. Por favor, inténtalo de nuevo más tarde.</p>
    </div>
</template>

<style scoped>
button {
    background-color: transparent;
    border: none;
}


.columns {
    display: flex;
    justify-content: space-between;
    height: 100%;
    margin-left: 5%;
    margin-right: 5%;
}

.columnLeft {
    width: 50%;
}

.columnRight {
    width: 100%;
    margin-top: 3em;
}

.songs {
    height: 400px; /* Define una altura fija para la sección de canciones */
    overflow-y: auto; /* Habilita el scroll vertical */
    overflow-x: hidden; /* Oculta el scroll horizontal */
    padding-right: 10px; /* Opcional: agrega espacio para evitar que el contenido se superponga con el scroll */
}

.fa-circle-play {
    color: rgb(30, 215, 96);
    font-size: 4em;
    margin-right: 0.7em;
    margin-left: 0.3em;
}

.song {
    background-color: rgb(244, 244, 244);
    border-radius: 1.5em;
    padding: 0.5em;
    margin: 1em;
    display: flex;
    align-items: center;
}

.horizontal-aling {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
}

p {
    font-size: 1.2em;
    margin: 0.3em;
}

i {
    transition: 0.2s ease-in;
}

i:hover {
    color: rgb(22, 164, 72);
}

</style>