<script setup>

    import axios from "axios";
    import {onMounted, inject} from "vue";
    import {ref} from "vue";
import { useRoute } from "vue-router";

    const globalState = inject('globalState')

    const props = defineProps({
        path: {
            type: String,
            default: "albums"
        }
    })

    const albums = ref([]);

    onMounted(() => {
        let path = "http://localhost:8081/" + props.path + "?";
        if (useRoute().path == "/artist/dashboard") {
            path = "http://localhost:8081/albums?artist=" + globalState.userId.value + "&";
        }
        path = path + "token=" + globalState.token.value;
        console.log(path);
        axios.get(path).then((response) => {
            albums.value = response.data;
            console.log(albums.value);
            console.log(albums.value.length);
        });
    });

</script>


<template>

    <div class="carousel">
        <div class="album" v-for="album in albums" :key="album.id">
            <p>{{ album.name }}</p>
        </div>
    </div>

</template>


<style scoped>
    .carousel {
        overflow: auto;
        white-space: nowrap;
    }

    .album {
        display: inline-flex;
        width: 25vh;
        height: 25vh;
        margin: 10px;
        background-color: red;
        justify-content: center;
        align-items: center;
    }
</style>