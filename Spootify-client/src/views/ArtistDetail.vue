<script setup>
    import { onMounted, ref, inject } from 'vue';
    import Songs from '../components/Songs.vue';
    import Albums from "../components/Albums.vue";
    import { useRoute } from 'vue-router';
    import axios from 'axios';

    let globalState = inject('globalState');

    const route = useRoute();

    const artist = ref("");
    const isLoaded = ref(false);

    onMounted(() => {
         let path = "http://localhost:8081/artists/" + route.params.id;
         axios.get(path).then((response) => {
             artist.value = response.data;
             isLoaded.value = true;
         }).catch((error) => {
             console.log(error);
         });
    });

    function getAllSongs(albums) {
        let allSongs = [];
        albums.forEach((album) => {
            allSongs = allSongs.concat(album.songs);
        });
        return allSongs;
    }
</script>

<template>
    <div class="template">
        <div id="divNombreArtista">
                    <h1>{{ artist.name }}</h1>
                </div>
        <div class="columns">
            <div class="column">
                <div id="followButtonDiv">
                    <button id="followButton">
                        Follow
                    </button>
                    <div class="artistInformation" v-if="isLoaded">
                        <div class="informationField">
                            <p>Followers:</p>
                            <p>5</p>
                        </div>
                        <div class="informationField">
                            <p>Albums:</p>
                            <p>{{ artist.albums.length }}</p>
                        </div>

                    </div>
                </div>
                <div id="divAlbums">
                    <h3>Albums</h3>
                    <Albums v-if="isLoaded" :albums="artist.albums"  />
                </div>

            </div>
            <div class="column">
                <Songs /> <!-- Esto no va de momento, cambiarlo va a doler-->
            </div>
        </div>
    </div>

</template>


<style scoped>
    .columns {
        display: flex;
        justify-content: space-between;
        border: 5px solid blue;
        height: 100%;
    }

    .column {
        border: 2px solid red;
        width: 50%;
        height: 68vh;
    }

    button {
        width: 70%;
        height: 5em;
        margin: 10px auto;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: #fff;
        background: rgb(30, 215, 96);
        font-size: 1em;
        font-weight: bold;
        outline: none;
        border: none;
        border-radius: 1em;
        transition: .2s ease-in;
        cursor: pointer;
    }

    button:disabled{
        background: rgb(113, 115, 114);
        cursor: unset;  
    }

    button:hover{
	    background: rgb(22, 164, 72);
    }

    button:disabled:hover{
        background: rgb(113, 115, 114);
        cursor: unset;  
    }

    .botonera {
        display: flex;
        justify-content: space-around;
        align-items: center;
        height: 50%;
        border: 1px solid aquamarine;
    }

    .inside-button {
        margin: 0.2em;
    }

    i {
        font-size: 4em;
    }

    .artistInformation {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 100%;
    }

    .artistInformation {
        display: flex;
        justify-content: center; /* Changed from center to space-around */
        align-items: center;
        width: 100%;
        margin-top: 15px;
        
    }
    #divAlbums {
    margin-top: 100px; /* Ajusta el valor según sea necesario */
}

    .informationField {
        display: flex;
        flex-direction: column; /* Stack elements vertically */
        align-items: center; /* Center horizontally */
        text-align: center; /* Center text */
        border-right: 2px solid gray;
        min-width: 20%;
    }

    .informationField:last-child {
        border-right: none; /* Remove right border from the last field */
    }

    .informationField p {
        margin: 0.5em; /* Remove default margin */
    }

</style>