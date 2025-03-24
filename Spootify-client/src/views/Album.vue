<script setup>
    import { onMounted } from 'vue';
    import axios from 'axios';
    import { useRoute } from 'vue-router';

    const route = useRoute();

    const album = ref();

    onMounted(() => {
        fetchAlbumDetails();
    });

    function fetchAlbumDetails() {
        let path = "http://localhost:8081/albums/" + route.params.id;
        axios.get(path).then((response) => {
            console.log(response.data);
            album.value = response.data;
            console.log(album.value);
        }).catch((error) => {
            console.log(error);
        });
    }



</script>


<template>

    <div class="template">
        <div class="column">
            <h2>{{ album.name }}</h2>
            <p v-for="artist in album.artists">{{ artist.name }}</p>
        </div>
        <div class="column">
            <div class="songs">
                <div class="song" v-for="song in album.songs" :key="song.id"> <!-- Key para reaccionar bien a los cambios-->
                    <i class="fa-solid fa-circle-play" @click="openLink(song.youtubeUrl)"></i>
                    <div class="horizontal-aling">
                        <div>
                            <p><b>{{ song.title }}</b></p>
                            <p><span class="name">{{ song.album.artists[0].name}}</span> - <span class="album"><i>{{ song.album.name }}</i></span></p>
                        </div>
                        <p>{{  song.duration }}</p>
                    </div>
                </div>
            </div>
        </div>
    </div>

</template>

<style scoped>

</style>