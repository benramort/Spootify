<script setup>
    import { onMounted, inject, ref } from "vue";
    import axios from "axios";


    const searchCont = ref('');
    const songsList = ref([]);
    const artistList = ref([]);
    const albumList = ref([]);

    function Search(){
      
        var pathSongs = "http://localhost:8081/songs/search?title=" + searchCont.value;
        var pathArtists = "http://localhost:8081/artists/search?name=" + searchCont.value;
        var pathAlbums = "http://localhost:8081/albums/search?name=" + searchCont.value;
        console.log(searchCont.value);
        songsList.value = [];
        artistList.value = [];
        albumList.value = [];

        axios.get(pathSongs).then((response) => {
            const uniqueSongs = response.data.filter(
                (song, index, self) =>
                index === self.findIndex((s) => s.id === song.id)
            );
            songsList.value = uniqueSongs;
            console.log("Canciones buscadas:");
            console.log(songsList.value);
        });

        axios.get(pathArtists).then((response) => {
            const uniqueArtists = response.data.filter(
                (artist, index, self) =>
                index === self.findIndex((a) => a.id === artist.id)
            );
            artistList.value = uniqueArtists;
            console.log("Artistas buscados:");
            console.log(artistList.value);
        });

        axios.get(pathAlbums).then((response) => {
            const uniqueAlbums = response.data.filter(
                (album, index, self) =>
                index === self.findIndex((a) => a.id === album.id)
            );
            albumList.value = uniqueAlbums;
            console.log("Álbumes buscados:");
            console.log(uniqueAlbums);
        });
        
    }
</script>

<template>
    <div id="searchBarDiv">
        <input type="text" id="searchBar" placeholder="Buscar..." v-model="searchCont"/>
        <button id="searchButton" @click="Search">Search</button>
    </div>
    <div id="resultDiv">
        <div id="artistResult" v-if="artistList.length > 0">
            <div class="artist" v-for="artist in artistList" :key="artist.id">
                        <div class="artist">
                            <i class="fa-solid fa-user"></i>
                            <router-link :to="`/artists/${artist.id}`">{{ artist.name }}</router-link>
                        </div>
                    </div>
        </div>
        <div id="artistResult" v-else>
            <p>No hay artistas con ese nombre</p>
        </div>
        <div id="albumResult" v-if="albumList.length > 0">
            <div class="album" v-for="album in albumList" :key="album.id">
                        <div class="album">
                            <router-link :to="`/albums/${album.id}`">{{ album.name }}</router-link>
                        </div>
                    </div>

            </div>
            <div id="albumResult" v-else>
                <p>No hay álbumes con ese nombre</p>
            </div>
        
       
        
        <div id="songsResult">
            <div class="songs">
                    <!-- Verifica si la playlist tiene canciones -->
                    <div v-if="songsList.length > 0">
                        <div class="song" v-for="song in songsList" :key="song.id">
                            <i class="fa-solid fa-circle-play" @click="openLink(song.youtubeUrl)"></i>
                            <div class="horizontal-aling">
                                <div>
                                    <p><b>{{ song.title }}</b></p>
                                </div>
                                <p>{{ song.duration }}</p>
                            </div>
                        </div>
                    </div>
                    <!-- Mensaje si no hay canciones -->
                    <div v-else>
                        <p>No hay canciones con ese nombre</p>
                    </div>
                </div>
        </div>

    </div>

</template>


<style scoped>
    .songs {
    height: 400px; /* Define una altura fija para la sección de canciones */
    overflow-y: auto; /* Habilita el scroll vertical */
    overflow-x: hidden; /* Oculta el scroll horizontal */
    padding-right: 10px; /* Opcional: agrega espacio para evitar que el contenido se superponga con el scroll */
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
a {
        text-decoration: none;
        color: black;
    }

    a:hover {
        text-decoration: underline;
    }

    .artist i {
        font-size: 2em;
        margin-right: 1.5em;
    }

    .artist {
        background-color: rgb(244, 244, 244);
        border-radius: 1.5em;
        padding: 0em;
        margin: 1em;
        display: flex;
        align-items: center;

    }

    .artists {
        height: 100%;
        overflow-y: auto;
        overflow-x: hidden;
    }

    .columns {
        display: flex;
        justify-content: space-between;
        height: 100%;
    }

    .column {
        width: 50%;
        height: 68vh;
        padding-right: 1em;
    }



    .botonera {
        margin-top: 2em;
        display: flex;
        justify-content: space-around;
        align-items: center;
        height: 40%;
        border: 1px solid aquamarine;
    }


    i {
        font-size: 4em;
    }

    .album {
        display: inline-flex;
        width: 30vh;
        height: 30vh;
        margin: 10px;
        background-color: red;
        justify-content: center;
        align-items: center;
        border: none;
        transition: all 0.3s ease;
        background-color: #282828; /* Dark gray like Spotify */
    }

    .album:hover {
        background-color: #333333; /* Slightly lighter on hover */
        transform: translateY(-5px);
    }

    .album p {
        color: white;
        font-size: 16px;
        font-weight: bold;
        margin: 10px 0 0;
        max-width: 100%;
        overflow: hidden;
        text-overflow: ellipsis;
    }
</style>