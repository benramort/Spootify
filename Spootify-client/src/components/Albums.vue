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
            const artistId = useRoute().path.substring(9);
            path = "http://localhost:8081/albums?artist=" + artistId + "&";
        }
        path = path + "token=" + globalState.token.value;
        axios.get(path).then((response) => {
            albums.value = response.data.map((album) => {
            if (album.cover && !album.cover.startsWith("http")) {
                album.cover = "http://localhost:8081/" + album.cover.substring(9);
            }
            return album;
        });
    }).catch((error) => {
        console.error("Error al obtener los álbumes:", error);
    });
    }

</script>


<template>

    <div class="carousel">
        <button 
        class="album" 
        v-for="album in albums" 
        :key="album.id" v-on:click="router.push('/albums/' + album.id);console.log(album.cover)"
        :style="{ backgroundImage: 'url(' + album.cover + ')' }">
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
        justify-content: center;
        align-items: center;
        border: none;
        transition: all 0.3s ease;
        background-size: cover;
        background-position: center;
        background-color: rgba(0, 0, 0, 0.4); /* Fallback color */
        background-blend-mode: darken;
    }

    .album:hover {
        background-color: rgba(255, 255, 255, 0.4); /* Slightly lighter on hover */
        background-blend-mode: saturation;
        transform: translateY(-5px);
    }

    .album p {
        color: white;
        font-size: 25px;
        font-weight: bold;
        margin: 10px 0 0;
        max-width: 100%;
        overflow: hidden;
        text-overflow: ellipsis;
    }
</style>