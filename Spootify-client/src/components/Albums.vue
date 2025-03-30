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
        albums: {
            type: Array,
            default: null
        }
    })

    const albums = ref([]);

    onMounted(() => {
        console.log(props.albums);
        if (props.albums == null) {
            getAlbums();
        } else {
            albums.value = props.albums;
        }
    });

    function getAlbums() {
        
        let path = "http://localhost:8081/" + props.path + "?";
        if (useRoute().path == "/artists/dashboard") {
            path = "http://localhost:8081/albums?artist=" + globalState.userId.value + "&";
        }else if(useRoute().path.startsWith("/artists/")){
            const artistId = useRoute().path.substring(9); // Extract the artist ID from the route
            path = "http://localhost:8081/albums?artist=" + artistId + "&";
            console.log("path: " + path);
        }
        path = path + "token=" + globalState.token.value;
        console.log(path);
        axios.get(path).then((response) => {
            albums.value = response.data;
            console.log(albums.value);
            console.log(albums.value.length);
        });
    }

</script>


<template>

    <div class="carousel">
        <button class="album" v-for="album in albums" :key="album.id" v-on:click="console.log('hasfd');router.push('/albums/' + album.id)">
            <p>{{ album.name }}</p>
        </button>
    </div>

</template>


<style scoped>
    .carousel {
        overflow: auto;
        white-space: nowrap;
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
    }

    .album:hover {
        background-color: blue;
    }
</style>