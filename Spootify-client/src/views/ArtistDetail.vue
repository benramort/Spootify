<script setup>
    import { onMounted, ref, inject } from 'vue';
    import Songs from '../components/Songs.vue';
    import Albums from "../components/Albums.vue";
    import { useRoute } from 'vue-router';
    import axios from 'axios';

    let globalState = inject('globalState');

    const route = useRoute();

    const name = ref("");

    onMounted(() => {
         let path = "http://localhost:8081/artists/" + route.params.id;
         console.log("Path detalle:"+path);
         axios.get(path).then((response) => {
             console.log(response.data);
             name.value = response.data.name;
             console.log(name.value);
         }).catch((error) => {
             console.log(error);
         });
    });
</script>

<template>
    <div class="template">
        <h1>{{ name }}</h1>
        <div class="columns">
            <div class="column">
                <div id="divNombreArtista">
                </div>
                <div id="followButtonDiv">
                    <button v-if="boton" id="followButton">
                        Follow
                    </button>
                    <button v-else id="followButton" disabled>
                        Following
                    </button>
                    <button id="followCountButton">
                        40 Followes
                    </button>
                </div>
                <div id="divAlbums">
                    <h3>Albums</h3>
                    <Albums />
                </div>

            </div>
            <div class="column">
                <div id="top5SongsDiv">
                    <Songs />
                </div>

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
        width: 49%;
        height: 150%;
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

    #followButtonDiv {
    display: flex;
    justify-content: space-between; /* Adds space between the buttons */
    align-items: center; /* Aligns buttons vertically in the center */
    gap: 10px; /* Adds spacing between buttons */
    }

    #followCountButton{
        cursor: unset;  
    }
    #followCountButton:hover{
        background: rgb(30, 215, 96);

        
    }
    #divAlbums {
    margin-top: 100px; /* Ajusta el valor según sea necesario */
}

</style>