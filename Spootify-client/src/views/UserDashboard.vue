<script setup>
    import { onMounted, ref, inject } from 'vue';
    import Songs from '../components/Songs.vue';
    import Albums from "../components/Albums.vue";
    import CreateAlbum from "../components/CreateAlbum.vue";
    import CreateSong from "../components/CreateSong.vue";
    import axios from 'axios';
    import PlaylistCarousel from '../components/PlaylistCarousel.vue';

    let globalState = inject('globalState');

    const user = ref();
    const isLoaded = ref(false);

    onMounted(() => {
        let path = "http://localhost:8081/users/myProfile";
        path += "?token=" + globalState.token.value;
        console.log(path);
        axios.get(path).then((response) => {
            console.log(response.data);
            user.value = response.data;
            isLoaded.value = true;
        }).catch((error) => {
            console.log(error);
        });
    });

</script>

<template>
    <div class="template">
        <h1>Hola, {{ user?.name }}:</h1>
        <div class="columns">
            <div class="column">
                <PlaylistCarousel v-if="isLoaded" :playlists="user?.playlists"/>
                <div class="artists">
                    <h2>Tus artistas seguidos: </h2>
                    <div class="artist" v-for="artist in user?.userFollows" :key="artist.id">
                        <div class="artist">
                            <i class="fa-solid fa-music"></i>
                            <router-link :to="`/artists/${artist.id}`">{{ artist.name }}</router-link>
                        </div>
                    </div>
                </div>
            </div>
            <div class="column">
                <Songs path="http://localhost:8081/songs?token={{ globalState.token.value }}" />
            </div>
        </div>
    </div>

</template>


<style scoped>

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
        border: 5px solid blue;
        height: 100%;
    }

    .column {
        border: 2px solid red;
        width: 50%;
        height: 68vh;
        padding-right: 1em;
    }

    button {
        width: 49%;
        height: 100%;
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

    button:hover{
	    background: rgb(22, 164, 72);
    }

    .botonera {
        margin-top: 2em;
        display: flex;
        justify-content: space-around;
        align-items: center;
        height: 40%;
        border: 1px solid aquamarine;
    }

    .inside-button {
        margin: 0.2em;
    }

    i {
        font-size: 4em;
    }
</style>