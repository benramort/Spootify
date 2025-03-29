<script setup>
    import { onMounted, ref, inject } from 'vue';
    import Songs from '../components/Songs.vue';
    import Albums from "../components/Albums.vue";
    import CreateAlbum from "../components/CreateAlbum.vue";
    import CreateSong from "../components/CreateSong.vue";
    import axios from 'axios';

    let globalState = inject('globalState');

    const name = ref("");
    const showModalAlbum = ref(false);
    const showModalSong = ref(false);

    onMounted(() => {
        let path = "http://localhost:8081/users/myProfile";
        path += "?token=" + globalState.token.value;
        console.log(path);
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
        <CreateAlbum :isOpen="showModalAlbum" @close="showModalAlbum = false" />
        <CreateSong :isOpen="showModalSong" @close="showModalSong = false" />
        <h1>Hola, {{ name }}:</h1>
        <div class="columns">
            <div class="column">
                <div class="botonera">                    
                </div>
            </div>
            <div class="column">
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