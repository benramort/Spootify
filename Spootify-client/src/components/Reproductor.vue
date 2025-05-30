<script setup>
import { ref, onMounted, computed, watch, inject } from 'vue';
import axios from 'axios';

const globalState = inject('globalState');

// Reactive state
const audioPlayer = ref(null);
const progress = ref(0);
const currentTime = ref('0:00');
const duration = ref('0:00');
const isPlaying = ref(false);
const volume = ref(80);
const isMuted = ref(false);
const showVolumeControl = ref(false);
const isShuffle = ref(false);
const isRepeat = ref(false);
const previousEnabled = ref(false);
const nextEnabled = ref(false);
const visible = ref(true);

const song = ref(null);
let queue = [];
let queueIndex = -1;

function playSong(songParam) {
  song.value = songParam;
  console.log("Portada before: " + songParam.album.cover);
  if(song.value.album.cover.startsWith("http")) {
    song.value.albumCover = song.value.album.cover;
  } else {
    song.value.albumCover = "http://localhost:8081/" + song.value.album.cover.substring(9);
  }
  
  setTimeout(
  () => {
      if (audioPlayer.value && songParam) {
      audioPlayer.value.src = `http://localhost:8081/stream?song=${songParam.id}`;
      audioPlayer.value.load(); // Important: reload the audio element
      }

      audioPlayer.value.play()
          .catch(err => console.error("Playback failed:", err));
      isPlaying.value = true;
  }, 100);

}

function selectSong(song) {
  queueIndex++;
  queue.splice(queueIndex, 0, song);
  playSong(song);
}

function nextSong() {
  if (!isRepeat.value) {
    if (isShuffle.value) {
      queueIndex = Math.floor(Math.random() * queue.length);
    } else {
      queueIndex++;
    }
  }
  console.log("Next song. Queue index: ", queueIndex, "Queue length:", queue.length, "Shuffle: ", isShuffle.value, "Repeat: ", isRepeat.value);
  if (queueIndex < queue.length) {
    playSong(queue[queueIndex]);
  }
  if (queueIndex >= 1) {
    previousEnabled.value = true;
  }
  if (queueIndex >= queue.length - 1 && !isShuffle.value) {
    console.log("Queue finished");
    nextEnabled.value = false;
  }
}

function previousSong() {
  queueIndex--;
  nextEnabled.value = true;
  if (queueIndex >= 0) {
    playSong(queue[queueIndex]);
  }
  if (queueIndex < 1) {
    previousEnabled.value = false;
  }
}


function addToQueue(songs) {
  nextEnabled.value = true;
  songs.forEach(song => {
    queue.push(song);
  });
  console.log("Index: ", queueIndex);
  console.log("Queue: ", queue);
}

// Format time functions
const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
};

// Computed properties
const progressStyle = computed(() => {
  return { width: `${progress.value}%` };
});

const volumeStyle = computed(() => {
  return { width: `${volume.value}%` };
});

// Player control functions
const togglePlay = () => {
  if (audioPlayer.value.paused) {
    audioPlayer.value.play();
    isPlaying.value = true;
  } else {
    audioPlayer.value.pause();
    isPlaying.value = false;
  }
};

const updateProgress = () => {
  if (audioPlayer.value) {
    const percent = (audioPlayer.value.currentTime / audioPlayer.value.duration) * 100;
    progress.value = isNaN(percent) ? 0 : percent;
    currentTime.value = formatTime(audioPlayer.value.currentTime);
    duration.value = formatTime(audioPlayer.value.duration);
  }
};

const setProgress = (e) => {
  const progressBar = e.currentTarget;
  const clickPosition = e.clientX - progressBar.getBoundingClientRect().left;
  const progressWidth = progressBar.clientWidth;
  const percentageClicked = (clickPosition / progressWidth) * 100;
  
  audioPlayer.value.currentTime = (percentageClicked / 100) * audioPlayer.value.duration;
};

const toggleMute = () => {
  audioPlayer.value.muted = !audioPlayer.value.muted;
  isMuted.value = audioPlayer.value.muted;
  if (isMuted.value) {
    volume.value = 0;
  } else {
    volume.value = 80; // default volume
  }
};

const setVolume = (e) => {
  const volumeBar = e.currentTarget;
  const clickPosition = e.clientX - volumeBar.getBoundingClientRect().left;
  const volumeWidth = volumeBar.clientWidth;
  
  volume.value = (clickPosition / volumeWidth) * 100;
  audioPlayer.value.volume = volume.value / 100;
  
  if (volume.value === 0) {
    isMuted.value = true;
  } else {
    isMuted.value = false;
  }
};

const likeSong = () => {
  console.log("Like button clicked");
  if (song.value.liked) {
    console.log("Canción ya está en la playlist 'Canciones que me gustan'");
    return;
  }
  const token = globalState.token.value;

  const path = `http://localhost:8081/songs/like?token=${token}&songId=${song.value.id}`;
  axios.post(path).then(() => {
      console.log("Canción añadida a la playlist 'Canciones que me gustan'");
      song.value.liked = true;
  }).catch((error) => {
      console.error("Error al añadir la canción a la playlist 'Canciones que me gustan':", error);
  });
};

const toggleShuffle = () => {
  isShuffle.value = !isShuffle.value;
};

const toggleRepeat = () => {
  isRepeat.value = !isRepeat.value;
};

// Load audio on mount
onMounted(() => {
  if (audioPlayer.value) {
    audioPlayer.value.volume = volume.value / 100;
  }
});

// Watch for audio time update
watch(() => audioPlayer.value, (newPlayer) => {
  if (newPlayer) {
    newPlayer.addEventListener('timeupdate', updateProgress);
    newPlayer.addEventListener('loadedmetadata', updateProgress);
    newPlayer.addEventListener('ended', nextSong)
  }
}, { immediate: true });

defineExpose({
  selectSong: selectSong,
  addToQueue: addToQueue
});
</script>

<template>
  <div class="player-wrapper" v-if="song">
    <!-- Hidden audio element -->
    <audio 
      ref="audioPlayer" 
      :src="`http://localhost:8081/stream?song=${song.id}`" 
      preload="metadata">
    </audio>
    
    <!-- Player UI -->
    <div class="player" v-if="visible">
      <!-- Left section: Album & Song info -->
      <div class="player-left">
        <div class="album-cover">
          <img :src="song.albumCover" alt="Album Cover" />
        </div>
        <div class="song-info">
          <div class="song-title">{{ song.title }}</div>
          <div class="song-artist">{{ song.album.artists[0].name }}</div>
        </div>
        <div class="like-button"
            @click="likeSong()"
            :disabled="song.liked"
            >
          <i :class="['fa', song.liked ? 'fa-heart' : 'fa-heart-o']"></i>
        </div>
      </div>
      
      <!-- Center section: Controls & Progress bar -->
      <div class="player-center">
        <div class="player-controls">
          <button class="control-button shuffle" @click="toggleShuffle" :class="{ active: isShuffle }">
            <i class="fa fa-random"></i>
          </button>
          <button class="control-button prev" :disabled="!previousEnabled" @click="previousSong">
            <i class="fa fa-step-backward"></i>
          </button>
          <button class="control-button play-pause" @click="togglePlay">
            <i :class="['fa', isPlaying ? 'fa-pause' : 'fa-play']"></i>
          </button>
          <button class="control-button next" :disabled="!nextEnabled" @click="nextSong">
            <i class="fa fa-step-forward"></i>
          </button>
          <button class="control-button repeat" @click="toggleRepeat" :class="{ active: isRepeat }">
            <i class="fa fa-repeat"></i>
          </button>
        </div>
        
        <div class="progress-container">
          <span class="time current">{{ currentTime }}</span>
          <div class="progress-bar" @click="setProgress">
            <div class="progress" :style="progressStyle"></div>
          </div>
          <span class="time total">{{ duration }}</span>
        </div>
      </div>
      
      <!-- Right section: Volume control, hide -->
      <div class="player-right">
        <button class="volume-button" @click="toggleMute">
          <i :class="['fa', isMuted ? 'fa-volume-off' : volume.value < 50 ? 'fa-volume-down' : 'fa-volume-up']"></i>
        </button>
        <div class="volume-control" v-show="showVolumeControl">
          <div class="volume-bar" @click="setVolume">
            <div class="volume-level" :style="volumeStyle"></div>
          </div>
        </div>
        <button class="down-button">
          <i class="fa fa-angle-down" @click="visible = false"></i>
        </button>
      </div>
    </div>

    <div v-if="!visible"> 
      <button class="down-button-floating" @click="visible = true">
        <i class="fa fa-angle-up"></i>
      </button>
    </div>
  </div>
</template>

<style scoped>
/* Player wrapper */
.player-wrapper {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
}

/* Main player container */
.player {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 90px;
  background-color: #181818;
  border-top: 1px solid #282828;
  padding: 0 16px;
  color: #fff;
  font-family: 'Spotify Circular', Arial, Helvetica, sans-serif;
}

/* Left section */
.player-left {
  display: flex;
  align-items: center;
  width: 30%;
  min-width: 180px;
}

.album-cover {
  width: 10%;
  height: 10%;
  margin-right: 14px;
  flex-shrink: 0;
}

.album-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.song-info {
  display: flex;
  flex-direction: column;
  margin-right: 14px;
  overflow: hidden;
}

.song-title {
  font-size: 14px;
  font-weight: 500;
  color: #fff;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.song-artist {
  font-size: 11px;
  color: #b3b3b3;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.like-button {
  color: #1ed760;
  font-size: 16px;
  margin-left: 8px;
  cursor: pointer;
}

/* Center section */
.player-center {
  max-width: 722px;
  width: 40%;
  padding: 0 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.player-controls {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.control-button {
  background: none;
  border: none;
  color: #b3b3b3;
  font-size: 14px;
  margin: 0 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.control-button:hover {
  color: #fff;
  transform: scale(1.05);
}

.control-button:disabled {
  color: #535353;
}

.control-button:disabled:hover {
  transform: none;
}

.shuffle, .repeat {
  font-size: 12px;
}

.play-pause {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #fff;
  color: #000;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 16px;
  font-size: 14px;
}

.play-pause:hover {
  transform: scale(1.06);
  color: #000;
}

.active {
  color: #1ed760;
}

.progress-container {
  width: 100%;
  display: flex;
  align-items: center;
}

.time {
  font-size: 11px;
  color: #b3b3b3;
  min-width: 40px;
}

.current {
  text-align: right;
  margin-right: 8px;
}

.total {
  text-align: left;
  margin-left: 8px;
}

.progress-bar {
  flex: 1;
  height: 4px;
  background-color: #535353;
  border-radius: 2px;
  position: relative;
  cursor: pointer;
}

.progress-bar:hover {
  height: 6px;
}

.progress {
  height: 100%;
  background-color: #b3b3b3;
  border-radius: 2px;
  position: absolute;
  left: 0;
  top: 0;
}

.progress-bar:hover .progress {
  background-color: #1ed760;
}

/* Right section */
.player-right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  width: 30%;
  min-width: 180px;
  position: relative;
}

.volume-button {
  background: none;
  border: none;
  color: #b3b3b3;
  font-size: 16px;
  cursor: pointer;
}

.volume-button:hover {
  color: #fff;
}

.volume-control {
  width: 100px;
  position: absolute;
  right: 60px;
  top: 50%;
  transform: translateY(-50%);
}

.volume-bar {
  height: 4px;
  background-color: #535353;
  border-radius: 2px;
  position: relative;
  cursor: pointer;
}

.volume-level {
  height: 100%;
  background-color: #b3b3b3;
  border-radius: 2px;
  position: absolute;
  left: 0;
  top: 0;
}

.volume-bar:hover .volume-level {
  background-color: #1ed760;
}

.down-button {
  background: none;
  border: none;
  color: #b3b3b3;
  font-size: 27px;
  cursor: pointer;
  margin-left: 8px;
  padding-bottom: 2px;
}

.down-button:hover {
  color: #fff;
}

.down-button-floating {
  position: fixed;
  bottom: 25px;
  right: 10px;
  background-color: #181818;
  color: #b3b3b3;
  border: none;
  font-size: 27px;
  padding-bottom: 2px;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: 0.2s;
}

.down-button-floating:hover {
  color: #fff;
  transform: scale(1.1);
  
}

/* Media queries for responsive design */
@media (max-width: 768px) {
  .player {
    padding: 0 8px;
  }
  
  .player-left {
    width: 40%;
    min-width: 120px;
  }
  
  .player-center {
    width: 60%;
  }
  
  .player-right {
    display: none;
  }
}

@media (max-width: 576px) {
  .player-left {
    width: 30%;
    min-width: 80px;
  }
  
  .song-artist {
    display: none;
  }
  
  .album-cover {
    width: 40px;
    height: 40px;
    margin-right: 8px;
  }
  
  .player-center {
    width: 70%;
  }
  
  .shuffle, .repeat {
    display: none;
  }
}
</style>