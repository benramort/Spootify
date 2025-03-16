<script setup>
    import axios from "axios";
    import {onMounted} from "vue";
    import {ref} from "vue";

    let songs = ref([]) //ref añade reactividad
    onMounted(() => {
        axios.get("http://localhost:8081/songs").then((response) => {
            songs.value = response.data;
            console.log(songs.value);
        });
    });

</script>


<template>
    <div class="song" v-for="song in songs" :key="song.title"> <!-- Key para reaccionar bien a los cambios-->
        <p>{{ song }}</p>
    </div>
    
</template>