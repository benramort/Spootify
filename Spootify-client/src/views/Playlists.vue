<script setup>
import router from "@/router";
import axios from "axios";
import { onMounted, inject, ref } from "vue";
import { useRoute } from "vue-router";
import CreatePlayList from "../components/CreatePlayList.vue";

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
    if(isNaN(globalState.token.value) || globalState.isArtist.value) {
        return;
    }
    let path = "http://localhost:8081/playlists";
    path = path + "?token=" + globalState.token.value;
    console.log(path);

    // Vacía el array antes de agregar nuevas playlists para evitar duplicados
    playlists.value = [];
        axios.get(path).then((response) => {
            const uniquePlaylists = response.data.filter(
                (playlist, index, self) =>
                index === self.findIndex((p) => p.id === playlist.id)
            );
            playlists.value = uniquePlaylists;
        });
});
</script>

<template>
    <div id="contMensajeError" v-if="!globalState.token.value || isNaN(globalState.token.value) || globalState.isArtist.value">
        <p id="mensajeError">Debes iniciar sesión antes de poder ver tus playlists.</p>
    </div>
    <div class="main-container" v-else>
        <div class="grid-container">
            <!-- Renderiza las playlists -->
            <button
                class="playlist"
                v-for="playlist in playlists"
                :key="playlist.id"
                @click="router.push('/playlists/' + playlist.id)"
            >
                <p>{{ playlist.name }}</p>
            </button>

            <!-- Botón de Crear Playlist siempre al final -->
            <button class="playlist create-button" @click="showModalAlbum = true">
                <i class="fa-solid fa-plus inside-button"></i>
                <p class="inside-button">Nueva playlist</p>
            </button>
        </div>

        <!-- Modal para crear una nueva playlist -->
        <CreatePlayList :isOpen="showModalAlbum" @close="showModalAlbum = false" />
    </div>
</template>

<style scoped>
.main-container {
    /* Changed min-height to height to take exactly the available space */
    height: calc(100vh - 80px); /* Account for header height */
    overflow-y: auto; /* Keep this to enable scrolling */
    margin: 0;
    padding: 20px;
    display: flex;
    flex-direction: column;
    /* Ensure the container takes the available width */
    width: 100%;
    box-sizing: border-box;
}

.grid-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 20px;
    width: 100%;
    max-width: 900px; /* Maximum width for very large screens */
    margin: 0 auto;
    padding: 20px;
}

.playlist {
    aspect-ratio: 1 / 1; /* This maintains square shape regardless of width */
    min-height: 180px;
    background-color: #282828; /* Dark gray like Spotify */
    border: none;
    /* border-radius: 8px; Slightly rounded corners like Spotify */
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    text-align: center;
    padding: 15px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.playlist:hover {
    background-color: #333333; /* Slightly lighter on hover */
    transform: translateY(-5px);
}

.playlist p {
    color: white;
    font-size: 16px;
    font-weight: bold;
    margin: 10px 0 0;
    max-width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
}

.create-button {
    background-color: rgb(30, 215, 96); /* Spotify green */
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.create-button:hover {
    background-color: rgb(29, 185, 84); /* Darker green on hover */
}

.inside-button {
    margin: 5px 0;
}

i.fa-plus {
    font-size: 2rem;
    margin-bottom: 10px;
}

/* Responsive adjustments */
@media (max-width: 768px) {
    .grid-container {
        grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
        padding: 10px;
    }
}

@media (max-width: 480px) {
    .grid-container {
        grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    }
}

#contMensajeError {
    margin: 0 auto;
    text-align: center;
}

#mensajeError {
    color: red;
    font-weight: bold;
    font-size: xx-large;
    font-family: 'Circular', sans-serif;
}
</style>