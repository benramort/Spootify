<script setup>
import { onMounted, ref, inject } from "vue";
import { useRoute } from "vue-router";
import { printDuration } from "../main.js";
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
            response.data.songs.forEach((song) => {
                song.duration = printDuration(song.duration);
            });
            playlist.value = response.data;
            console.log("Publica: " + playlist.value.isPublic);
            console.log("User: " + playlist.value.user);
        })
        .catch((error) => {
            console.error("Error al cargar la playlist:", error);
            playlist.value = null; // Maneja el error asignando null
        });

}

function generateShareLink() {

    let path = `http://localhost:8081/playlists/${playlist.value.id}/share?token=${globalState.token.value}`;
    let link = `http://localhost:8080/playlists/${playlist.value.id}`;
    
    window.navigator.clipboard.writeText(link).then(() => {
        alert("Enlace copiado al portapapeles");
    }).catch(err => {
        console.error('Error al copiar el enlace: ', err);
    });
    
    axios.post(path, {id: playlist.value.id})
        .then((response) => {
            console.log("La playlist pasa a ser pública", response.data);
        })
        .catch((error) => {
            console.error("Error al hacer pública la playlist:", error);
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
        <div class="playlistHeader">
            <div id="nameAndShare">
                <h2 id="playlistName">{{ playlist.name }}</h2>
            </div>
            <div id="shareAndPlay">
                <img @click="generateShareLink()" id="shareImg" src="../assets/Share_button.png" alt="Compartir Playlist">
                <button id="playAllButton">
                    <i class="fa-solid fa-circle-play" @click="playAll()"></i>
                </button>
            </div>
        </div>
        <div class="songs">
            <!-- Verifica si la playlist tiene canciones -->
            <Songs v-if="playlist.songs.length > 0" :songs="playlist.songs" />
            <!-- Mensaje si no hay canciones -->
            <div v-if="playlist.songs.length === 0">
                <p>Esta playlist no tiene canciones.</p>
            </div>
        </div>
    </div>
    <div v-else>
        <p>No se pudo cargar la playlist. Por favor, inténtalo de nuevo más tarde.</p>
    </div>
</template>

<style scoped>
#playlistName {
    font-size: 2em;
    text-align: center;
    margin: 0 auto;
    align-items: center;
    padding: 0%;
    margin-left: 75px;
}

.playlistHeader {
    display: flex;
    align-items: center;
    width: 30%;
    margin: 0 auto;
    margin-top: 5em;
    margin-bottom: 4.6em;
}

#playAllButton {
    margin-left: 1em;
}

#shareAndPlay {
    display: flex;
    align-items: center;
    margin: 0 auto;
    text-align: center;
}

#shareImg {
    width: 1.2em;
    height: 1.2em;
    cursor: pointer;
}

#shareImg:hover {
    transform: translateY(-5px);
    transition: 0.3s ease;
}

button {
    background-color: transparent;
    border: none;
    cursor: pointer;
}

.songs {
    margin: 0 auto;
    height: 400px; /* Define una altura fija para la sección de canciones */
    overflow-y: auto; /* Habilita el scroll vertical */
    overflow-x: hidden; /* Oculta el scroll horizontal */
    padding-right: 10px; /* Opcional: agrega espacio para evitar que el contenido se superponga con el scroll */
    width: 85%;
}

.fa-circle-play {
    color: rgb(30, 215, 96);
    font-size: 3em;
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