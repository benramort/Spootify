<script setup>

    import router from "@/router";
    import axios from "axios";
    import {onMounted, inject} from "vue";
    import {ref} from "vue";
    import { useRoute, useRouter } from "vue-router";

    const globalState = inject('globalState')

    const props = defineProps({
        path: {
            type: String,
            default: "albums"
        },
        playlists: {
            type: Array,
            default: null
        }


    })

    const playlists = ref([]);

    onMounted(() => {
        playlists.value = props.playlists;
    });

    function getPlaylists() {
        let path = "http://localhost:8081/" + props.path + "?";
        if (useRoute().path == "/artists/dashboard") {
            path = "http://localhost:8081/albums?artist=" + globalState.userId.value + "&";
        }else if(useRoute().path.startsWith("/artists/")){
            const artistId = useRoute().path.substring(9); // Extract the artist ID from the route
            path = "http://localhost:8081/albums?artist=" + artistId + "&";
        }
        path = path + "token=" + globalState.token.value;
        axios.get(path).then((response) => {
            albums.value = response.data;
        });
    }
</script>


<template>

    <div class="carousel">
        <button class="playlist" v-for="playlist in playlists" :key="playlist.id" v-on:click="router.push('/playlists/' + playlist.id)">
            <p>{{ playlist.name }}</p>
        </button>
    </div>

</template>


<style scoped>
    .carousel {
        overflow: auto;
        white-space: nowrap;
    }

    .playlist {
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

    .playlist:hover {
        background-color: #333333; /* Slightly lighter on hover */
        transform: translateY(-5px);
    }

    .playlist p {
        color: white;
        font-size: 16px;
        font-weight: bold;
        margin: 10px 0 0;
        max-width: 100%;
        overflow: hidden;
        text-overflow: ellipsis;
    }
</style>