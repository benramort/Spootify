<template>
    <div class="search-container">
      <!-- Search Bar -->
      <div class="search-bar-container">
        <div class="search-bar">
          <i class="fa fa-search search-icon"></i>
          <input 
            type="text" 
            placeholder="Search for artists, songs, or albums..." 
            v-model="searchCont"
            @keyup.enter="Search"
            @keyup="debounceSearch"
          />
          <button class="search-button" @click="Search">Search</button>
        </div>
      </div>
  
      <!-- Results Section -->
      <div class="search-results" v-if="hasResults">
        <!-- Artists Section -->
        <section class="result-section" v-if="artistList.length > 0">
          <h2>Artists</h2>
          <div class="artist-results">
            <div class="artist-card" v-for="artist in artistList" :key="artist.id">
              <div class="artist-image">
                <i class="fa-solid fa-user"></i>
              </div>
              <div class="artist-info">
                <router-link :to="`/artists/${artist.id}`">{{ artist.name }}</router-link>
                <span class="artist-type">Artist</span>
              </div>
            </div>
          </div>
        </section>
  
        <!-- Albums Section -->
        <section class="result-section" v-if="albumList.length > 0">
          <h2>Albums</h2>
          <div class="album-results">
            <div class="album-card" v-for="album in albumList" :key="album.id">
              <div class="album-cover" 
                   :style="album.cover ? { backgroundImage: `url(http://localhost:8081/${album.cover})` } : {}">
                <i v-if="!album.cover" class="fa fa-music album-fallback"></i>
              </div>
              <div class="album-info">
                <router-link :to="`/albums/${album.id}`">{{ album.name }}</router-link>
                <span class="album-artist" v-if="album.artist">{{ album.artist.name }}</span>
              </div>
            </div>
          </div>
        </section>
  
        <!-- Songs Section -->
        <section class="result-section" v-if="songsList.length > 0">
          <h2>Songs</h2>
          <div class="songs-table">
            <div class="songs-header">
              <div class="song-number">#</div>
              <div class="song-title">Title</div>
              <div class="song-album">Album</div>
              <div class="song-duration">Duration</div>
            </div>
            
            <div class="song-row" v-for="(song, index) in songsList" :key="song.id">
              <div class="song-number">{{ index + 1 }}</div>
              <div class="song-title">
                <div class="song-play">
                  <i class="fa-solid fa-circle-play" @click="playSong(song)"></i>
                </div>
                <div class="song-info">
                  <p class="title">{{ song.title }}</p>
                  <p class="artist" v-if="song.album && song.album.artists && song.album.artists.length">
                    {{ song.album.artists[0].name }}
                  </p>
                </div>
              </div>
              <div class="song-album">
                <router-link v-if="song.album" :to="`/albums/${song.album.id}`">
                  {{ song.album.name }}
                </router-link>
                <span v-else>Unknown Album</span>
              </div>
              <div class="song-duration">{{ formatDuration(song.duration) }}</div>
            </div>
          </div>
        </section>
      </div>
  
      <!-- No Results Message -->
      <div class="no-results" v-else-if="searchCont && !isLoading">
        <i class="fa fa-search"></i>
        <h2>No results found for "{{ searchCont }}"</h2>
        <p>Please try again with different keywords</p>
      </div>
      
      <!-- Welcome Message (Initial State) -->
      <div class="search-welcome" v-else-if="!searchCont">
        <i class="fa fa-headphones"></i>
        <h2>Search for your favorite music</h2>
        <p>Find artists, albums, and songs</p>
      </div>
      
      <!-- Loading State -->
      <div class="loading" v-else>
        <div class="loading-spinner"></div>
        <p>Searching...</p>
      </div>
    </div>
  </template>
  
  <script setup>
  import { onMounted, inject, ref, computed } from "vue";
  import axios from "axios";
  
  const searchCont = ref('');
  const songsList = ref([]);
  const artistList = ref([]);
  const albumList = ref([]);
  const isLoading = ref(false);
  let searchTimeout = null;
  
  // Check if there are any results
  const hasResults = computed(() => {
    return songsList.value.length > 0 || artistList.value.length > 0 || albumList.value.length > 0;
  });
  
  // Format duration from seconds to mm:ss
  function formatDuration(seconds) {
    if (!seconds) return '0:00';
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs < 10 ? '0' + secs : secs}`;
  }
  
  // Function to play a song (you can inject your reproductor here)
  function playSong(song) {
    console.log('Playing song:', song);
    // If you have a reproductor component:
    // reproductor.selectSong(song);
  }

  function debounceSearch(event) {
    // Ignore certain keys
    if (['Tab', 'Shift', 'Alt', 'Control', 'Meta'].includes(event.key)) {
        return;
    }
    
    // Clear the previous timeout
    if (searchTimeout) {
        clearTimeout(searchTimeout);
    }
    
    // Set a new timeout
    searchTimeout = setTimeout(() => {
        if (searchCont.value.trim().length >= 2) { // Only search with 2+ characters
        Search();
        }
    }, 500); // Wait 500ms after user stops typing
    }
  
  function Search() {
    if (!searchCont.value.trim()) return;
    
    isLoading.value = true;
    
    const pathSongs = `http://localhost:8081/songs/search?title=${encodeURIComponent(searchCont.value)}`;
    const pathArtists = `http://localhost:8081/artists/search?name=${encodeURIComponent(searchCont.value)}`;
    const pathAlbums = `http://localhost:8081/albums/search?name=${encodeURIComponent(searchCont.value)}`;
    
    // Clear previous results
    songsList.value = [];
    artistList.value = [];
    albumList.value = [];
  
    // Create an array of promises for all API calls
    const promises = [
      axios.get(pathSongs).then(response => {
        const uniqueSongs = response.data.filter(
          (song, index, self) => index === self.findIndex((s) => s.id === song.id)
        );
        songsList.value = uniqueSongs;
        console.log("Songs found:", uniqueSongs);
      }),
      
      axios.get(pathArtists).then(response => {
        const uniqueArtists = response.data.filter(
          (artist, index, self) => index === self.findIndex((a) => a.id === artist.id)
        );
        artistList.value = uniqueArtists;
        console.log("Artists found:", uniqueArtists);
      }),
      
      axios.get(pathAlbums).then(response => {
        const uniqueAlbums = response.data.filter(
          (album, index, self) => index === self.findIndex((a) => a.id === album.id)
        );
        albumList.value = uniqueAlbums;
        console.log("Albums found:", uniqueAlbums);
      })
    ];
    
    // Wait for all requests to complete
    Promise.all(promises)
      .catch(error => {
        console.error("Error during search:", error);
      })
      .finally(() => {
        isLoading.value = false;
      });
  }
  </script>
  
  <style scoped>
  /* Modern, clean styling with Spotify-inspired design */
  .search-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    font-family: 'Helvetica Neue', Arial, sans-serif;
  }
  
  /* Search Bar */
  .search-bar-container {
    margin-bottom: 30px;
  }
  
  .search-bar {
    display: flex;
    align-items: center;
    background: #fff;
    border-radius: 30px;
    padding: 5px 20px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    position: relative;
  }
  
  .search-icon {
    color: #777;
    margin-right: 10px;
  }
  
  .search-bar input {
    flex-grow: 1;
    border: none;
    padding: 12px 5px;
    font-size: 16px;
    outline: none;
    background: transparent;
  }
  
  .search-button {
    background: #1DB954; /* Spotify green */
    color: white;
    border: none;
    border-radius: 24px;
    padding: 10px 24px;
    font-size: 14px;
    font-weight: bold;
    cursor: pointer;
    transition: all 0.2s ease;
  }
  
  .search-button:hover {
    background: #1ed760;
    transform: scale(1.05);
  }
  
  /* Results Sections */
  .result-section {
    margin-bottom: 40px;
  }
  
  .result-section h2 {
    font-size: 24px;
    margin-bottom: 16px;
    color: #333;
    font-weight: 700;
    border-bottom: 1px solid #eee;
    padding-bottom: 10px;
  }
  
  /* Artist Cards */
  .artist-results {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
  }
  
  .artist-card {
    display: flex;
    align-items: center;
    padding: 15px;
    background: #f7f7f7;
    border-radius: 8px;
    width: calc(33.333% - 20px);
    min-width: 250px;
    transition: all 0.3s ease;
  }
  
  .artist-card:hover {
    background: #f0f0f0;
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  }
  
  .artist-image {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 60px;
    height: 60px;
    background: #ddd;
    border-radius: 50%;
    margin-right: 15px;
  }
  
  .artist-image i {
    font-size: 24px;
    color: #555;
  }
  
  .artist-info {
    display: flex;
    flex-direction: column;
  }
  
  .artist-info a {
    color: #333;
    font-weight: 700;
    text-decoration: none;
    font-size: 16px;
    margin-bottom: 4px;
  }
  
  .artist-info a:hover {
    color: #1DB954;
    text-decoration: underline;
  }
  
  .artist-type {
    color: #777;
    font-size: 14px;
  }
  
  /* Album Cards */
  .album-results {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
  }
  
  .album-card {
    width: 180px;
    transition: all 0.3s ease;
  }
  
  .album-card:hover {
    transform: translateY(-5px);
  }
  
  .album-cover {
    width: 180px;
    height: 180px;
    background-color: #333;
    background-size: cover;
    background-position: center;
    border-radius: 8px;
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
  
  .album-fallback {
    font-size: 50px;
    color: #555;
  }
  
  .album-info {
    display: flex;
    flex-direction: column;
  }
  
  .album-info a {
    color: #333;
    font-weight: 700;
    text-decoration: none;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    margin-bottom: 4px;
  }
  
  .album-info a:hover {
    color: #1DB954;
    text-decoration: underline;
  }
  
  .album-artist {
    color: #777;
    font-size: 14px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  /* Songs Table */
  .songs-table {
    width: 100%;
    background: #fff;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  }
  
  .songs-header {
    display: flex;
    padding: 15px;
    border-bottom: 1px solid #eee;
    font-weight: 600;
    color: #777;
    font-size: 14px;
  }
  
  .song-row {
    display: flex;
    align-items: center;
    padding: 12px 15px;
    border-bottom: 1px solid #f5f5f5;
    transition: background 0.2s ease;
  }
  
  .song-row:hover {
    background: #f9f9f9;
  }
  
  .song-number {
    width: 30px;
    color: #999;
    font-size: 14px;
    text-align: right;
    margin-right: 15px;
  }
  
  .song-title {
    flex: 1;
    display: flex;
    align-items: center;
    min-width: 0;
  }
  
  .song-play {
    margin-right: 15px;
  }
  
  .song-play i {
    font-size: 24px;
    color: #1DB954;
    opacity: 0.8;
    cursor: pointer;
    transition: all 0.2s ease;
  }
  
  .song-play i:hover {
    opacity: 1;
    transform: scale(1.1);
  }
  
  .song-info {
    min-width: 0;
  }
  
  .song-info .title {
    font-weight: 600;
    margin-bottom: 4px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .song-info .artist {
    color: #777;
    font-size: 14px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .song-album {
    flex: 1;
    color: #555;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    margin-right: 15px;
  }
  
  .song-album a {
    color: #555;
    text-decoration: none;
  }
  
  .song-album a:hover {
    color: #1DB954;
    text-decoration: underline;
  }
  
  .song-duration {
    width: 60px;
    text-align: right;
    color: #777;
  }
  
  /* Empty states */
  .no-results, .search-welcome, .loading {
    text-align: center;
    padding: 60px 0;
    color: #777;
  }
  
  .no-results i, .search-welcome i {
    font-size: 60px;
    margin-bottom: 20px;
    color: #ddd;
  }
  
  .no-results h2, .search-welcome h2 {
    font-size: 24px;
    margin-bottom: 10px;
    color: #555;
  }
  
  .no-results p, .search-welcome p {
    font-size: 16px;
  }
  
  /* Loading spinner */
  .loading-spinner {
    display: inline-block;
    width: 50px;
    height: 50px;
    border: 3px solid rgba(29, 185, 84, 0.3);
    border-radius: 50%;
    border-top-color: #1DB954;
    animation: spin 1s ease-in-out infinite;
    margin-bottom: 20px;
  }
  
  @keyframes spin {
    to { transform: rotate(360deg); }
  }
  
  /* Responsive design */
  @media (max-width: 768px) {
    .artist-card {
      width: calc(50% - 20px);
    }
    
    .song-album {
      display: none;
    }
  }
  
  @media (max-width: 480px) {
    .artist-card {
      width: 100%;
    }
    
    .album-card {
      width: 140px;
    }
    
    .album-cover {
      width: 140px;
      height: 140px;
    }
    
    .search-button {
      padding: 8px 16px;
    }
  }
  </style>