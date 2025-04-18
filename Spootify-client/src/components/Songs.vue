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
                    <!-- Botón de añadir -->
                    <button class="add-button" @click="openPlaylistModal(song)">+</button>
                    <!-- Botón de corazón -->
                    <button 
                        class="heart-button" 
                        @click="() => { toggleFavorite(song); addToPlaylistMeGustan(song); }">
                        <i :class="{'fa-solid fa-heart': song.isFavorite, 'fa-regular fa-heart': !song.isFavorite}"></i>
                    </button>
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
            song.isFavorite = false; // Inicializa el estado de favorito
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

function addToPlaylistMeGustan(song) {
    // Realiza una llamada a la API para obtener el perfil del usuario
    const token = globalState.token.value; // Asegúrate de que el token esté disponible
    axios
        .get(`http://localhost:8081/users/myProfile?token=${token}`)
        .then((response) => {
            const userName = response.data.name; // Obtiene el nombre del usuario del perfil
            console.log("Nombre del usuario:", userName);

            // Construye el nombre de la playlist
            const playlistName = `Canciones que me gustan de ${userName}`;
            console.log("Buscando playlist con nombre:", playlistName);

            // Busca la playlist en la lista de playlists
            const playlist = playlists.value.find((playlist) => playlist.name === playlistName);

            if (playlist && playlist.id) {
                // Si encuentra la playlist, utiliza su ID en el path
                const path = `http://localhost:8081/playlists/${playlist.id}/songs?token=${token}`;
                axios
                    .post(path, { id: song.id })
                    .then(() => {
                        console.log(`Canción añadida a la playlist: ${playlist.name}`);
                    })
                    .catch((error) => {
                        console.error("Error al añadir la canción a la playlist:", error);
                    });
            } else {
                // Si no encuentra la playlist, muestra un mensaje de error
                console.error(`No se encontró una playlist válida con el nombre '${playlistName}' o el ID es null.`);
            }
        })
        .catch((error) => {
            console.error("Error al obtener el perfil del usuario:", error);
        });
}

function toggleFavorite(song) {
    song.isFavorite = !song.isFavorite; // Cambia el estado de favorito
    console.log(`Canción ${song.title} es favorita: ${song.isFavorite}`);
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

.heart-button {
    background-color: transparent;
    color: rgb(30, 215, 96);
    border: none;
    font-size: 1.2em;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
    transition: transform 0.2s ease-in-out;
}

.heart-button:hover {
    transform: scale(1.2); /* Aumenta el tamaño al pasar el cursor */
}

.fa-heart {
    font-size: 1.5em;
}

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
    background-color: white;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    text-align: center;
}

.playlist-scroll {
    max-height: 200px;
    overflow-y: auto;
    margin: 10px 0;
}

.close-button {
    margin-top: 10px;
    background-color: red;
    color: white;
    border: none;
    border-radius: 5px;
    padding: 5px 10px;
    cursor: pointer;
}

.close-button:hover {
    background-color: darkred;
}
</style>