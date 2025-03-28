<template>
    <Modal :isOpen="showModalSong" @close="showModalSong = false">
        <div id="contFormulario">
            <div id="titulo">
                <p id="crearAlbum">CREAR CANCIÓN</p>
            </div>
            <div id="campos">
                <div class="campo" id="campoNombre">
                    <label for="nombre"></label>
                    <input id="inputNombre" type="text" placeholder="Name" v-model="songName" />
                </div>
                <div class="campo" id="campoAlbum">
                    <label for="album"></label>
                    <select id="inputAlbum" v-model="album">
                        <option v-for="album in albums" :key="album.id" :value="album.name">{{ album.name }}</option>
                    </select>
                </div>
                <div class="campo" id="campoDuracion">
                    <input id="inputDuracion" type="number" placeholder="Duration" v-model="duration">
                </div>
                <div class="campo" id="campoUrl">
                    <input id="inputUrl" type="text" placeholder="YouTube URL" v-model="youtubeUrl">
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
    height: 262px;
    background-color: rgb(34, 34, 34);
    box-shadow: 5px 10px 20px black;
    border-radius: 10px;
}

#campos {
    height: 250px;
    flex-direction: column;
    text-align: center;
    gap: 15px;
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
    padding-top: 10px;
}

#campoNombre {
    align-items: center;
    margin: 0 auto;
    margin-top: 10px;
}

input {
    background-color: white;
    width: 150px;
    height: 23px;
    margin-left: 10px;
    border-radius: 5px;
    border-color: white;
    border: 0px;
}

#crearAlbum {
    margin-top: 0px;
    padding-top: 15px;
    margin: 0 auto;
}

#button {
    display: inline-block;
    align-self: flex-end;
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
    align-items: center;
}
</style>

<script setup>
import { inject, ref, onMounted } from 'vue';
import Modal from "../components/CreateAlbumModal.vue";
import axios from 'axios';

const globalState = inject("globalState");
const showModalSong = ref(false);
const albumName = ref("");
const albums = ref([]);

function createAlbum() {
    let path = "http://localhost:8081/albums";
    path += "?token=" + globalState.token.value;
    axios.post(path, {"name": albumName.value}).then((response) => {
        console.log(response);
        console.log("Album created");
        location.reload();
    }).catch((error) => {
        console.log(error);
    });
}

function getAlbums() {
    let path = "http://localhost:8081/albums"
    path += "?token=" + globalState.token.value;
    axios.get(path).then((response) => {
        albums.value = response.data.value;
        console.log("Albums: " + albums);
        return albums;
    }).catch((error) => {
        console.log(error);
    });
}

onMounted(() => {
    getAlbums();
})
</script>