<script setup>
    import Prueba from '../components/Songs.vue';
</script>

<template>
    <h1>Hola, NombreDeUsuario:</h1>
    <div class="columns">
        <div class="column">

        </div>
        <div class="column">
            <Prueba path="songs"/>
        </div>

    </div>

</template>


<style>
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