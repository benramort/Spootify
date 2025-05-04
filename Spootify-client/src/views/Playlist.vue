<script setup>
import { onMounted, ref, inject } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";

const globalState = inject("globalState");

const route = useRoute();

const playlist = ref({
    name: "",
    songs: [],
});

const reproductor = inject("reproductor");

function play(song) {
    reproductor.playSong(song);
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

function openLink(link) {
    window.open(link, "_blank");
}
</script>

<template>
    <div v-if="playlist.name">
        <div class="columns">
            <div class="columnLeft">
                <div id="nameAndShare">
                    <h2 id="playlistName">{{ playlist.name }}</h2>
                    <img id="shareImg" src="../assets/Share_button.png" alt="Compartir Playlist">
                </div>
            </div>
            <div class="columnRight">
                <div class="songs">
                    <!-- Verifica si la playlist tiene canciones -->
                    <div v-if="playlist.songs.length > 0">
                        <div class="song" v-for="song in playlist.songs" :key="song.id">
                            <i class="fa-solid fa-circle-play" @click="play(song)"></i>
                            <div class="horizontal-aling">
                                <div>
                                    <p><b>{{ song.title }}</b></p>
                                </div>
                                <p>{{ song.duration }}</p>
                            </div>
                        </div>
                    </div>
                    <!-- Mensaje si no hay canciones -->
                    <div v-else>
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
#shareImg {
    width: 1.5em;
    height: 1.5em;
    rotate: 15deg;
    cursor: pointer;
    margin-left: 7px;
}

#shareImg:hover {
    transform: translateY(-5px);
    transition: 0.3s ease;
}

#nameAndShare {
    display: flex;
    align-items: center;
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