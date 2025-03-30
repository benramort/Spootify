<script setup>
    import axios from "axios";
    import {onMounted, inject} from "vue";
    import {ref} from "vue";
    import { useRoute } from "vue-router";
    import { printDuration } from "../main.js";

    const globalState = inject('globalState')

    const props = defineProps({
        songs: {
            type: Array,
            default: null
        }

        
    })

    let songs = ref([]) //ref añade reactividad
    onMounted(() => {
        if (props.songs == null) {
            getSongs();
        } else {
            songs.value = props.songs;
        }
    });

    function getSongs() {
        console.log(globalState);
        let actualPath = useRoute().path;
        let path = "http://localhost:8081/songs?";
        if (actualPath == "/artists/dashboard") {
            path = "http://localhost:8081/songs?artistId=" + globalState.userId.value + "&";
        }else if (useRoute().path.startsWith("/artists/")){
            const artistId = useRoute().path.substring(9); // Extract the artist ID from the route
            path = "http://localhost:8081/songs?artistId=" + artistId + "&";
            console.log("path: " + path);
        }
        path = path+"token="+globalState.token.value
        console.log(path)
        axios.get(path).then((response) => {
            songs.value = response.data;
            console.log(songs.value);
            songs.value.forEach((song) => {
                song.duration = printDuration(song.duration);
            });
        });
    }

    function openLink(link) {
        // console.log(link);
        window.open(link, "_blank");
    }

</script>


<template>
    <div class="songs">
        <div class="song" v-for="song in songs" :key="song.title"> <!-- Key para reaccionar bien a los cambios-->
            <i class="fa-solid fa-circle-play" @click="openLink(song.youtubeUrl)"></i>
            <div class="horizontal-aling">
                <div>
                    <p><b>{{ song.title }}</b></p>
                    <p><span class="name"><router-link :to="`/artists/${song.album.artists[0].id}`">{{ song.album.artists[0].name}}</router-link></span> - <span class="album"><router-link :to="`/albums/${song.album.id}`"><em>{{ song.album.name }}</em></router-link></span></p>
                </div>
                <p>{{  song.duration }}</p>
            </div>
        </div>
    </div>
    
    
</template>

<style scoped>
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
    }

    /* i {
        transition: .2s ease-in;
    }

    i:hover{
	    color: rgb(22, 164, 72);
    } */

    a {
        color: black;
        text-decoration: none;
    }
    
    a:hover {
        text-decoration: underline;
    }
</style>