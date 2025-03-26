<template>
    <!-- <button @click="showModal = true">Crear album</button> -->
    <Modal :isOpen="showModal" @close="showModal = false">
        <div id="contFormulario">
            <div id="titulo">
                <p id="crearAlbum">CREAR ÁLBUM</p>
            </div>
            <div id="campos">
                <div id="campoNombre">
                    <input id="inputNombre" type="text" placeholder="Name" v-model="albumName" />
                </div>
                <div id="button">
                    <button @click="$emit('close'); console.log(albumName); createAlbum();" id="okButton">✔</button>
                </div>
            </div>
        </div>
    </Modal>
</template>

<style scoped>
#contFormulario {
    width: 300px;
    height: 120px;
    border: 2px solid rgb(34, 34, 34);
    margin: 0 auto;
    background-color: rgb(34, 34, 34);
    box-shadow: 5px 10px 20px black;
    border-radius: 10px;
}

#titulo {
    margin: 0 auto;
    text-align: center;
    width: 300px;
    height: 50px;
    color: white;
    font-weight: bold;
    margin-bottom: 0px;
    margin-top: 8px;
    padding-bottom: 0px;
}

#campoNombre {
    display: flex;
    align-items: center;
    margin: 0 auto;
    margin-top: 20px;
    margin-bottom: 20px;
    display: inline-block;
}

#labelNombre {
    margin-left: 50px;
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

#crearAlbum {
    margin-top: 0px;
    padding-top: 15px;
    margin: 0 auto;
}

#button {
    display: inline-block;
    margin-right: 40px;
}

#okButton {
    background-color: rgb(30, 215, 96);
    color: black;
    border-radius: 5px;
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
    align-items: center;
}
</style>

<script setup>
import { inject, ref } from 'vue';
import Modal from "../components/CreateAlbumModal.vue";
import axios from 'axios';

const globalState = inject("globalState");
const showModal = ref(false);
const albumName = ref("");

function createAlbum() {
    let path = "http://localhost:8081/albums";
    path += "?token=" + globalState.token.value;
    axios.post(path, {"name": albumName.value}).then((response) => {
        console.log(response);
        console.log("Album created");
    }).catch((error) => {
        console.log(error);
    });
}
</script>
