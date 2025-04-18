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

// Recupera el token del almacenamiento local al cargar el componente
if (!globalState.token.value) {
    const savedToken = localStorage.getItem("authToken");
    if (savedToken) {
        globalState.token.value = savedToken;
    }
}

// Guarda el token en el almacenamiento local al iniciar sesión
function saveToken(token) {
    globalState.token.value = token;
    localStorage.setItem("authToken", token);
}

onMounted(() => {
    if(globalState.isArtist.value == true) {
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
        console.log(playlists.value);
        console.log(playlists.value.length);
    });
});
</script>

<template>
    <div class="main-container">
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
    height: 100vh; /* Ocupa toda la altura de la ventana */
    overflow-y: auto; /* Habilita el scroll vertical si el contenido excede la altura */
    margin: 0; /* Elimina márgenes predeterminados */
    display: flex;
    flex-direction: column; /* Asegura que los elementos se apilen verticalmente */
}

.grid-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); /* Ajusta el tamaño mínimo y máximo */
    gap: 20px; /* Espacio uniforme entre las playlists */
    padding: 40px;
    margin: 0 auto; /* Centra el bloque horizontalmente */
    max-width: 60%; /* Define un ancho máximo relativo para que haya el mismo espacio a los lados */
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

.create-button {
    background-color: green; /* Color diferente para el botón de crear */
    color: white;
}

.create-button:hover {
    background-color: darkgreen; /* Color al pasar el cursor */
}
</style>