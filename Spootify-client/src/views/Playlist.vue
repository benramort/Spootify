<script setup>
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";

const globalState = inject("globalState");

const route = useRoute();

const playlist = ref({
    name: "",
    songs: [],
});

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
    <div v-if="playlist">
        <div class="columns">
            <div class="columnLeft">
                <h2>{{ playlist.name }}</h2>
            </div>
            <div class="columnRight">
                <div class="songs">
                    <div class="song" v-for="song in playlist.songs" :key="song.id">
                        <i class="fa-solid fa-circle-play" @click="openLink(song.youtubeUrl)"></i>
                        <div class="horizontal-aling">
                            <div>
                                <p><b>{{ song.title }}</b></p>
                            </div>
                            <p>{{ song.duration }}</p>
                        </div>
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
    height: 100%;
    overflow-y: auto;
    overflow-x: hidden;
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