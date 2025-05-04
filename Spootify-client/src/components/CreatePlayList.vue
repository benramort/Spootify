<template>
    <Modal :isOpen="showModalPlayList" @close="closeModal">
        <div :class="['contFormulario', { 'contFormularioError': errorMessage }]">
            <div id="titulo">
                <p id="crearPlayList">NUEVA PLAYLIST</p>
            </div>
            <!-- Mensaje de error entre el título y el cuadro de texto -->
            <div v-if="errorMessage" id="mensajeError">
                <p id="errorMessage">{{ errorMessage }}</p>
            </div>
            <div id="campos">
                <div id="campoNombre">
                    <input id="inputNombre" type="text" placeholder="Name" v-model="playListName" />
                </div>
                <div id="button">
                    <button @click="handleCreatePlayList" id="okButton">✔</button>
                </div>
            </div>
        </div>
    </Modal>
</template>

<style scoped>
#crearPlayList {
    color: white;
    font-family: 'Circular', sans-serif;
}

.contFormulario {
    width: 300px;
    height: 120px; /* Ajusta la altura para incluir el mensaje de error */
    border: 2px solid rgb(34, 34, 34);
    margin: 0 auto;
    background-color: rgb(34, 34, 34);
    box-shadow: 5px 10px 20px black;
    border-radius: 10px;
}

.contFormularioError {
    height: 140px; /* Ajusta la altura para incluir el mensaje de error */
}

#errorMessage {
    color: red;
    font-size: 0.9em;
    text-align: center;
    margin-top: 10px;
    margin-bottom: 10px; /* Espacio entre el mensaje y el cuadro de texto */
}

#titulo {
    margin: 0 auto;
    text-align: center;
    width: 300px;
    height: 50px;
    color: white;
    font-weight: bold;
    margin-bottom: 0px;
    margin-top: 0px;
    padding-top: 10px;
    padding-bottom: 0px;
}

#campoNombre {
    margin: 0 auto;
    margin-top: 10px;
    margin-bottom: 20px;
    display: inline-block;
}

#inputNombre {
    background-color: white;
    width: 150px;
    height: 23px;
    margin-left: 20px;
    border-radius: 5px;
    border-color: white;
    margin: 0 auto;
    border: 0px;
    margin-left: 20px;
}

#button {
    display: inline-block;
    margin-right: 40px;
    margin-top: 0px;
    padding-top: 0px;
    margin-top: 11px;
}

#okButton {
    background-color: rgb(30, 215, 96);
    color: black;
    border-radius: 1000px;
    border-color: rgb(30, 215, 96);
    margin-right: 10px;
    width: 30px;
    height: 25px;
    border: 0px;
    transition: 0.2s ease-in;
    cursor: pointer;
}

#okButton:hover {
    background: rgb(22, 164, 72);
}

#campos {
    display: flex;
    margin-top: 0px;
    text-align: center;
    margin: 0 auto;
    margin-left: 10px;
}

#errorMessage {
    color: red;
    text-align: center;
    margin-bottom: 0px;
    padding-bottom: 0px;
    margin-top: 0px;
    padding-top: 0px;
}

#mensajeError {
    margin: 0 auto;
    text-align: center;
    margin-bottom: 5px;
    padding-bottom: 0px;
    margin-top: 0px;
    padding-top: 0px;
}
</style>

<script setup>
import { inject, ref } from 'vue';
import Modal from "../components/CreateAlbumModal.vue";
import axios from 'axios';

const globalState = inject("globalState");
const showModalPlayList = ref(false);
const playListName = ref("");
const errorMessage = ref(""); // Variable para almacenar el mensaje de error

function handleCreatePlayList() {
    if (!playListName.value.trim()) {
        errorMessage.value = "Todos los campos son obligatorios";
        return;
    }

    createPlayList();
}

function createPlayList() {
    let path = "http://localhost:8081/playlists";
    path += "?token=" + globalState.token.value;

    axios.post(path, { "name": playListName.value })
        .then((response) => {
            console.log(response);
            console.log("Playlist created");
            location.reload();
        })
        .catch((error) => {
            console.log(error);
            errorMessage.value = "Error al crear la playlist. Inténtalo de nuevo.";
        });
}

function closeModal() {
    showModalPlayList.value = false;
    errorMessage.value = "";
    playListName.value = "";
}
</script>