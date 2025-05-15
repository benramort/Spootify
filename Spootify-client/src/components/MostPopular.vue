<script setup>
import { ref, onMounted, inject } from 'vue';
import axios from 'axios';

const mostFollowedArtists = ref([]);
const mostLikedSongs = ref([]);
const isLoaded = ref(false);

const reproductor = inject("reproductor");

function playSong(song) {
    reproductor.selectSong(song);
}

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
                    <router-link :to="`/artists/${artist.id}`">{{ artist.name }}</router-link>
                    <span class="followers-count">{{ artist.followersList?.length || 0 }} seguidores</span>
                    <div v-if="idx < mostFollowedArtists.length - 1" class="divider"></div>
                </div>
            </div>
            <div v-else>
                <p>Cargando artistas...</p>
            </div>
        </div>
        <div class="right">
            <h2>Canciones con más likes</h2>
            <div v-if="isLoaded">
                <div
                    class="song"
                    v-for="(song, idx) in mostLikedSongs"
                    :key="song.id"
                >
                    <span class="ranking">{{ idx + 1 }}.</span>
                    <span class="song-title">
                        {{ song.title }}
                    </span>
                    <span class="likes-count">
                        {{ song.numeroLikes || 0 }} <i class="fa-solid fa-heart"></i>
                    </span>
                    <button class="play-btn" @click="playSong(song)" title="Reproducir">
                        ▶
                    </button>
                    <div v-if="idx < mostLikedSongs.length - 1" class="divider"></div>
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
    /* Quitar fondo */
    background: none;
    border-radius: 1.5em;
    padding: 1.5em;
    box-shadow: none;
}
.artist {
    border-radius: 1.5em;
    padding: 0.5em 1em;
    margin: 1em 0;
    display: flex;
    align-items: center;
    background: none;
    justify-content: space-between;
    position: relative;
    flex-wrap: wrap;
}
.song {
    background: none !important;
    border-radius: 1.5em;
    padding: 0.5em 1em;
    margin: 1em 0;
    display: flex;
    align-items: center;
    position: relative;
    flex-wrap: wrap;
}
.divider {
    width: 100%;
    height: 1px;
    background: #e0e0e0;
    margin: 0.5em 0 0 0;
    position: absolute;
    left: 0;
    bottom: -0.5em;
}
.ranking {
    font-weight: bold;
    font-size: 1.3em;
    margin-right: 1em;
    color: #000000;
}
.song-title {
    flex: 1;
    font-size: 1.1em;
    color: #222;
    font-weight: 500;
    margin-left: 0.5em;
    text-decoration: none;
    transition: text-decoration 0.2s, color 0.2s;
}
.likes-count {
    display: flex;
    align-items: center;
    margin-right: 1em;
    color: #1db954;
    font-weight: 500;
    font-size: 1.1em;
    gap: 0.3em;
}
.play-btn {
    background: #1db954;
    border: none;
    color: white;
    border-radius: 50%;
    width: 2.2em;
    height: 2.2em;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.2em;
    cursor: pointer;
    margin-left: auto;
    transition: background 0.2s;
}
.play-btn:hover {
    background: #169943;
}
.followers-count {
    margin-left: auto;
    color: #888;
    font-size: 1em;
    font-weight: 500;
}
a {
    text-decoration: none;
    color: black;
}
a:hover {
    text-decoration: underline;
}

</style>