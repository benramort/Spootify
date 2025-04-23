<template>
    <Modal :isOpen="showModalAlbum" @close="closeModal">
        <div :class="['contFormulario', { 'contFormularioError': errorMessage }]">
            <div id="titulo">
                <p id="crearAlbum">NUEVO ÁLBUM</p>
            </div>
            <div id="mensajeError">
                <p v-if="errorMessage" id="errorMessage">{{ errorMessage }}</p>
            </div>
            <div id="campos">
                <div id="campoNombre">
                    <input id="inputNombre" type="text" placeholder="Name" v-model="albumName" />
                </div>
                <div id="portadaYBoton">
                    <div id="campoPortada">
                        <input id="inputPortada" type="file" @change="(event) => { handleFileChange(event); console.log('archivo cargado'); }" />
                    </div>
                    <div id="button">
                        <button @click="handleCreateAlbum()" id="okButton">✔</button>
                    </div>
                </div>
            </div>
        </div>
    </Modal>
</template>

<style scoped>
#crearAlbum {
    color: white;
    font-family: 'Circular', sans-serif;
}

.contFormulario {
    width: 450px;
    height: 140px;
    border: 2px solid rgb(34, 34, 34);
    margin: 0 auto;
    background-color: rgb(34, 34, 34);
    box-shadow: 5px 10px 20px black;
    border-radius: 10px;
}

.contFormularioError {
    height: 140px;
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
    margin-top: 0px;
    margin-bottom: 10px;
}

#campoPortada {
    margin-top: 20px;
    margin: 0 auto;
    margin-top: 0px;
    margin-bottom: 0px;
}

#inputNombre {
    background-color: white;
    width: 150px;
    height: 23px;
    margin-left: 20px;
    border-radius: 5px;
    margin: 0 auto;
    border: 0px;
    margin-left: 0px;
}

#inputPortada {
    background-color: white;
    width: 320px;
    height: 23px;
    margin-left: 20px;
    border-radius: 5px;
    margin: 0 auto;
    border: 0px;
    margin-left: 30px;
}

#button {
    margin-right: 0px;
    margin-top: 0px;
    padding-top: 0px;
    margin-top: 0px;
    margin-left: 20px;
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
    flex-direction: column;
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

#portadaYBoton {
    margin: 0 auto;
    display: flex; 
    align-items: center; 
    justify-content: center;  
}
</style>

<script setup>
import { inject, ref } from 'vue';
import Modal from "../components/CreateAlbumModal.vue";
import axios from 'axios';

const globalState = inject("globalState");
const showModalAlbum = ref(false);
const albumName = ref("");
const errorMessage = ref("");
const coverImage = ref(null);

function handleFileChange(event) {
    coverImage.value = event.target.files[0];
    console.log("Imagen:" + coverImage.value);
}

function validateFields() {
    return albumName.value.trim() !== "";
}

function handleCreateAlbum() {
    if (!validateFields()) {
        errorMessage.value = "Todos los campos son obligatorios";
        return;
    }
    createAlbum();
    showModalAlbum.value = false;
}

function createAlbum() {
    if (!globalState.token.value || isNaN(globalState.token.value)) {
        return;
    }    

    let path = "http://localhost:8081/albums";

    const formData = new FormData();
    formData.append("name", albumName.value);
    if(coverImage.value) {
        formData.append("cover", coverImage.value);
    }
    formData.append("token", globalState.token.value);

    axios.post(path, formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    }).then(() => {
        for (let pair of formData.entries()) {
            console.log(`${pair[0]}: ${pair[1]}`);
        }
        location.reload();
    }).catch((error) => {
        console.log(error);
    });
}

function closeModal() {
    showModalAlbum.value = false;
    errorMessage.value = "";
    albumName.value = "";
}
</script>