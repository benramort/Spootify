<script setup>
    import axios from "axios";
    import {onMounted} from "vue";
    import {ref} from "vue";

    let songs = ref([]) //ref añade reactividad
    onMounted(() => {
        axios.get("http://localhost:8081/songs").then((response) => {
            songs.value = response.data;
            // console.log(songs.value);
            songs.value.forEach((song) => {
                song.duration = printDuration(song.duration);
            });
        });
    });

    function printDuration(seconds) {
        let minutes = Math.floor(seconds / 60);
        let remainingSeconds = seconds % 60;
        return `${minutes}:${remainingSeconds}`;
    }

    function openLink(link) {
        // console.log(link);
        window.open(link, "_blank");
    }

</script>


<template>
    <div class="song" v-for="song in songs" :key="song.title"> <!-- Key para reaccionar bien a los cambios-->
        <i class="fa-solid fa-circle-play" @click="openLink(song.youtubeUrl)"></i>
        <div class="horizontal-aling">
            <div>
                <p><b>{{ song.title }}</b></p>
                <p>{{ song.artist.name }}</p>
            </div>
            <p>{{  song.duration }}</p>
        </div>
    </div>
    
</template>

<style scoped>
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

    p {
        margin: 0.3em;
    }
</style>