<script setup>
    import { onMounted, ref, inject } from 'vue';
    import axios from 'axios';
    import { useRoute } from 'vue-router';
    import { printDuration } from '../main.js';
    import Songs from "../components/Songs.vue";

    const reproductor = inject("reproductor");

    function play(song) {
        song.album = album.value;
        reproductor.selectSong(song);
    }

    function playAll() {
        try {
            album.value.songs.forEach(s => s.album = album.value);
            let albumWithoutFirst = album.value.songs.slice(1, album.value.songs.length);
            console.log(albumWithoutFirst);
            reproductor.addToQueue(albumWithoutFirst);
            reproductor.selectSong(album.value.songs[0]);
        } catch (error) {
            console.log(error);
        }
    }
        

    const route = useRoute();

    const album = ref({});

    onMounted(() => {
        fetchAlbumDetails();
        console.log('Al cargar ' + album.value.cover);
    });

    function fetchAlbumDetails() {
        let path = "http://localhost:8081/albums/" + route.params.id;
        console.log('Params: ' + route.params.id);
        console.log('Cover ' + album.cover);
        axios.get(path).then((response) => {
            album.value = response.data;
            album.value.cover = album.value.cover.substring(22);
            console.log('Portada antes ' + album.value.cover);
            album.value.cover = "http://localhost:8081/" + album.value.cover.substring(9);
            console.log('Portada despues ' + album.value.cover);
            album.value.songs.forEach((song) => {
                song.duration = printDuration(song.duration);
            });
            return album;
        }).catch((error) => {
            console.log(error);
        });
    }



</script>


<template>


    <div v-if="album.name">
        
        <div class="columns">
            <div class="columnLeft">
                <div class="cover" :style="{ backgroundImage: 'url(' + album.cover + ')' }">
                </div>
                <h2>{{ album.name }}</h2>
                <p v-for="artist in album.artists"><router-link :to="`/artists/${artist.id}`">{{ artist.name }}</router-link></p>
                <button>
                    <i class="fa-solid fa-circle-play" @click="playAll()"></i>
                </button>
            </div>
            <div class="columnRight">
                <div class="songs">
                    <!-- Verifica si la playlist tiene canciones -->
                    <Songs v-if="album.songs.length > 0" :songs="album.songs" />
                    <!-- Mensaje si no hay canciones -->
                    <div v-if="album.songs.length === 0">
                        <p>Este album no tiene canciones.</p>
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

button {
    background-color: transparent;
    border: none;
}

.cover {
    display: inline-flex;
    width: 30vh;
    height: 30vh;
    margin: 0px;
    justify-content: center;
    align-items: center;
    border: none;
    transition: all 0.3s ease;
    background-size: cover;
    background-position: center;
    box-shadow: 7px 7px 25px #2a2a2a;
}

.columnLeft div {
    margin-top: 25px; /* Agrega espacio encima de la cabecera */
}

h2 {
    font-family: 'Circular', 'sans-serif';
    font-size: xx-large;
    margin: 0;
    margin-top: 20px;
}


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

    .album {
        margin-left: 0.3em;
        font-size: 0.8em;
    }

    .name {
        margin-right: 0.3em;
    }

    p {
        font-size: 1.2em;
        margin: 0.3em;
        margin-left: 0;
    }

    i {
        transition: .2s ease-in;
    }

    i:hover{
	    color: rgb(22, 164, 72);
    }

    a {
        color: black;
        text-decoration: none;
    }

    #errorNoCanciones {
        color: red;
        text-align: center;
        font-weight: bold;
        font-family: 'Circular', 'sans-serif';
        font-size: xx-large;
        margin: 0 auto;
        margin-bottom: 0px;
        padding-bottom: 0px;
        margin-top: 0px;
        padding-top: 0px;
    }

    a:hover {
        text-decoration: underline;
    }
</style>