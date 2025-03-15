<script setup>
    let songs = [{ title: 'Song 1', artist: 'Artist 1' }, { title: 'Song 2', artist: 'Artist 2' }]
</script>


<template>
    <div class="song" v-for="song in songs">
        <h3>{{ song.title }}</h3>
        <p>{{ song.artist }}</p>
    </div>
    
</template>