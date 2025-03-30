<script setup>
    import { inject, onMounted } from 'vue';
import Prueba from '../components/Songs.vue';
    import Login from '../views/Login.vue'
import router from '@/router';

    const globalState = inject('globalState');

    onMounted(() => {
        if(isNaN(globalState.token.value)) {
            return;
        } else {
            if(globalState.isArtist.value) {
                router.push('/artists/dashboard');
            } else {
                router.push('/users/dashboard')
            }
        }

    });
</script>

<template>
    <!-- <h1>Hola, NombreDeUsuario:</h1>
    <div class="columns">
        <div class="column">

        </div>
        <div class="column">
            <Prueba path="songs"/>
        </div>

    </div> -->

    <div id="contLogo">

    </div>
    <div id="contLoggearse">
        <Login/>
    </div>

</template>


<style scoped>
    .columns {
        display: flex;
        justify-content: space-between;
        border: 5px solid blue;
    }

    .column {
        border: 2px solid red;
        width: 50%;
    }
</style>