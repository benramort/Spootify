<template>
    <div class="songs">
        <div class="song" v-for="song in songs" :key="song.title">
            <i class="fa-solid fa-circle-play" @click="openLink(song.youtubeUrl)"></i>
            <div class="horizontal-aling">
                <div>
                    <p><b>{{ song.title }}</b></p>
                    <p>
                        <span class="name">
                            <router-link :to="`/artists/${song.album.artists[0].id}`">{{ song.album.artists[0].name }}</router-link>
                        </span>
                        -
                        <span class="album">
                            <router-link :to="`/albums/${song.album.id}`"><em>{{ song.album.name }}</em></router-link>
                        </span>
                    </p>
                </div>
                <div class="song-actions">
                    <p>{{ song.duration }}</p>
                    <button v-if="globalState.isArtist.value === false" class="add-button" @click="openPlaylistModal(song)">+</button>
                </div>
            </div>
        </div>

        <!-- Modal para seleccionar la playlist -->
        <div v-if="showModal" class="modal-overlay">
            <div class="modal">
                <h3>Selecciona una playlist</h3>
                <div class="playlist-scroll">
                    <ul>
                        <li v-for="playlist in playlists" :key="playlist.id">
                            <button @click="addToPlaylist(playlist, selectedSong)">{{ playlist.name }}</button>
                        </li>
                    </ul>
                </div>
                <button class="close-button" @click="closeModal">Cerrar</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import axios from "axios";
import { onMounted, inject, ref } from "vue";
import { useRoute } from "vue-router";
import { printDuration } from "../main.js";

const globalState = inject("globalState");

const props = defineProps({
    path: {
        type: String,
        default: "songs",
    },
});

let songs = ref([]);
let playlists = ref([]); // Lista de playlists
let showModal = ref(false); // Controla la visibilidad del modal
let selectedSong = ref(null); // Canción seleccionada para añadir a una playlist

onMounted(() => {
    console.log(globalState);
    let actualPath = useRoute().path;
    let path = "http://localhost:8081/songs?";
    if (actualPath == "/artists/dashboard") {
        path = "http://localhost:8081/songs?artistId=" + globalState.userId.value + "&";
    } else if (useRoute().path.startsWith("/artists/")) {
        const artistId = useRoute().path.substring(9);
        path = "http://localhost:8081/songs?artistId=" + artistId + "&";
        console.log("path: " + path);
    }
    path = path + "token=" + globalState.token.value;
    console.log(path);
    axios.get(path).then((response) => {
        songs.value = response.data;
        console.log(songs.value);
        songs.value.forEach((song) => {
            song.duration = printDuration(song.duration);
        });
    });

    // Cargar las playlists del usuario
    const playlistPath = `http://localhost:8081/playlists?token=${globalState.token.value}`;
    axios.get(playlistPath).then((response) => {
        playlists.value = response.data;
    });
});

function openLink(link) {
    window.open(link, "_blank");
}

function openPlaylistModal(song) {
    selectedSong.value = song; // Guarda la canción seleccionada
    showModal.value = true; // Muestra el modal
}

function closeModal() {
    showModal.value = false; // Oculta el modal
    selectedSong.value = null; // Limpia la canción seleccionada
}

function addToPlaylist(playlist, song) {
    const path = `http://localhost:8081/playlists/${playlist.id}/songs?token=${globalState.token.value}`;
    axios.post(path, { id: song.id }).then(() => {
        console.log(`Canción añadida a la playlist: ${playlist.name}`);
        closeModal(); // Cierra el modal después de añadir la canción
    }).catch((error) => {
        console.error("Error al añadir la canción a la playlist:", error);
    });
}
</script>

<style scoped>
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

.song-actions {
    display: flex;
    align-items: center;
    gap: 10px;
}

.add-button {
    background-color: rgb(30, 215, 96);
    color: white;
    border: none;
    border-radius: 50%;
    width: 30px;
    height: 30px;
    font-size: 1.2em;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
    transition: background-color 0.2s ease-in-out;
}

.add-button:hover {
    background-color: rgb(22, 164, 72);
}

/* Estilo de la ventana para añadir una canción a una playlist */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
}

.modal {
    background-color: rgb(0, 0, 0);
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    text-align: center;
    border: 2px solid rgb(30, 215, 96); /* Borde del modal */
    display: flex;
    flex-direction: column; /* Asegura que los elementos estén en una columna */
    align-items: center; /* Centra todos los elementos horizontalmente */
}

.modal h3 {
    color: rgb(30, 215, 96); /* Título del modal */
    margin-bottom: 20px; /* Espaciado inferior */
}

.playlist-scroll {
    max-height: 200px;
    overflow-y: auto;
    margin: 10px 0;
    display: flex;
    flex-direction: column; /* Asegura que los botones estén en una columna */
    align-items: center; /* Centra los botones horizontalmente */
}

.playlist-scroll::-webkit-scrollbar {
    width: 10px; /* Ancho del scroll */
    background-color: transparent; /* Sin color de fondo */
}

.playlist-scroll::-webkit-scrollbar-thumb {
    background-color: rgb(30, 215, 96); /* Color verde para el thumb (barra de desplazamiento) */
    border-radius: 5px; /* Bordes redondeados */
}

.playlist-scroll::-webkit-scrollbar-thumb:hover {
    background-color: rgb(22, 164, 72); /* Color más oscuro al pasar el cursor */
}

.playlist-scroll::-webkit-scrollbar-button {
    background-color: rgb(30, 215, 96); /* Color verde para las flechas */
    height: 10px; /* Altura de las flechas */
}

.playlist-scroll::-webkit-scrollbar-button:hover {
    background-color: rgb(22, 164, 72); /* Color más oscuro al pasar el cursor sobre las flechas */
}

/* Elimina los puntos de la lista */
.playlist-scroll ul {
    list-style: none; /* Elimina los puntos de la lista */
    padding: 0; /* Elimina el padding por defecto */
    margin: 0; /* Elimina el margen por defecto */
    width: 100%; /* Asegura que la lista ocupe todo el ancho del modal */
}

.playlist-scroll li {
    margin-bottom: 10px; /* Espaciado entre elementos de la lista */
    display: flex;
    justify-content: center; /* Centra los botones dentro de cada elemento */
}

/* Estilo para truncar títulos largos de las playlists */
.playlist-scroll button {
    white-space: nowrap; /* Evita que el texto se divida en varias líneas */
    overflow: hidden; /* Oculta el texto que no cabe */
    text-overflow: ellipsis; /* Muestra los tres puntos (...) */
    max-width: 100%; /* Limita el ancho del botón */
    display: inline-block; /* Asegura que el botón respete las reglas de truncamiento */
    background-color: rgb(30, 215, 96); /* Color de fondo del botón */
    color: white; /* Color del texto */
    border: none; /* Sin bordes */
    border-radius: 5px; /* Bordes redondeados */
    padding: 5px 10px; /* Espaciado interno */
    cursor: pointer; /* Cambia el cursor al pasar sobre el botón */
    margin: 5px 0; /* Espaciado entre botones */
    transition: background-color 0.2s ease-in-out; /* Transición suave al cambiar el color */
}

.playlist-scroll button:hover {
    background-color: rgb(22, 164, 72); /* Color de fondo al pasar el cursor */
}

.close-button {
    margin-top: 20px; /* Espaciado superior para separarlo de los botones de las playlists */
    background-color: rgb(30, 215, 96);
    color: white;
    border: none;
    border-radius: 5px;
    padding: 5px 10px;
    cursor: pointer;
    display: block; /* Asegura que el botón ocupe una línea completa */
    margin-left: 48%; /* Centra horizontalmente */
    margin-right: 52%; /* Centra horizontalmente */
}

.close-button:hover {
    background-color: rgb(22, 164, 72);
}

a {
    color: black;
    text-decoration: none;
}

a:hover {
    text-decoration: underline;
}

i:hover {
    color: rgb(22, 164, 72);
}
</style>