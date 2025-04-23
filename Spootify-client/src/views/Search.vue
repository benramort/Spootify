<script setup>
    import { onMounted, inject, ref } from "vue";
    import axios from "axios";


    const searchCont = ref('');
    const songsList = ref([]);

    function Search(){
      
        var pathSongs = "http://localhost:8081/songs/search?title=" + searchCont.value;
        console.log(pathSongs);
        console.log(searchCont.value);
        songsList.value = [];
        axios.get(pathSongs).then((response) => {
            const uniqueSongs = response.data.filter(
                (song, index, self) =>
                index === self.findIndex((s) => s.id === song.id)
            );
            songsList.value = uniqueSongs;
            console.log("Canciones buscadas:");
            console.log(songsList.value);
        });

        
    }
</script>

<template>
    <div id="searchBarDiv">
        <input type="text" id="searchBar" placeholder="Buscar..." v-model="searchCont"/>
        <button id="searchButton" @click="Search">Search</button>
    </div>
    <div id="resultDiv">
        <div id="artistResult"></div>
        <div id="albumResult"></div>
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
</style>