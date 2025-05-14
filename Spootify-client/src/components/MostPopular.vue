<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import Songs from './Songs.vue';

const mostFollowedArtists = ref([]);
const mostLikedSongs = ref([]);
const isLoaded = ref(false);

onMounted(async () => {
    try {
        // Obtener artistas más seguidos
        const artistsRes = await axios.get('http://localhost:8081/artists/mostfollowedartists');
        mostFollowedArtists.value = artistsRes.data;

        // Obtener canciones más gustadas
        const songsRes = await axios.get('http://localhost:8081/songs/mostlikedsongs');
        mostLikedSongs.value = songsRes.data;

        isLoaded.value = true;
    } catch (error) {
        console.error('Error cargando datos populares:', error);
    }
});
</script>

<template>
    <div class="most-popular">
        <div class="left">
            <h2>Artistas más seguidos</h2>
            <div v-if="isLoaded">
                <div class="artist" v-for="(artist, idx) in mostFollowedArtists" :key="artist.id">
                    <span class="ranking">{{ idx + 1 }}.</span>
                    <i class="fa-solid fa-user"></i>
                    <router-link :to="`/artists/${artist.id}`">{{ artist.name }}</router-link>
                </div>
            </div>
            <div v-else>
                <p>Cargando artistas...</p>
            </div>
        </div>
        <div class="right">
            <h2>Canciones con más likes</h2>
            <div v-if="isLoaded">
                <div class="song" v-for="(song, idx) in mostLikedSongs" :key="song.id" style="display: flex; align-items: center;">
                    <span class="ranking">{{ idx + 1 }}.</span>
                    <div style="flex: 1;">
                        <Songs :songs="[song]" />
                    </div>
                </div>
            </div>
            <div v-else>
                <p>Cargando canciones...</p>
            </div>
        </div>
    </div>
</template>

<style scoped>
.most-popular {
    display: flex;
    justify-content: space-between;
    height: 100%;
    padding: 2em;
}
.left, .right {
    width: 48%;
    background: #fafafa;
    border-radius: 1.5em;
    padding: 1.5em;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.artist, .song {
    background-color: rgb(244, 244, 244);
    border-radius: 1.5em;
    padding: 0.5em 1em;
    margin: 1em 0;
    display: flex;
    align-items: center;
}
.artist i, .song i {
    font-size: 2em;
    margin-right: 1.5em;
}
.ranking {
    font-weight: bold;
    font-size: 1.3em;
    margin-right: 1em;
    color: #2e7d32;
}
a {
    text-decoration: none;
    color: black;
}
a:hover {
    text-decoration: underline;
}
</style>