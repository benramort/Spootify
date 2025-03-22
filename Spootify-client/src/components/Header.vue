<script setup>
// You can import any necessary components or define props here
</script>

<template>
  <div class="header">
    <img src="../assets/Spootify_logo.png" alt="Spotify logo"/>
    <div class="side">
    <div class="header-box"> <router-link to="/login">Home</router-link></div>
    <div class="header-box"><router-link to="/artist/dashboard">Home</router-link></div>
    <div class="header-box"><router-link to="/login">Home</router-link></div>
    <div class="header-box"><router-link to="/login">Home</router-link></div>
    <div class="header-box"><router-link to="/login">Home</router-link></div>
    </div>
  </div>
</template>

<style scoped>
img {
    width: 4em;
    height: 4em;
    margin: 0.5em;
}

a {
  color: white;
  text-decoration: none;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

/* Style for exact active links (optional) */
a.router-link-exact-active {
  text-decoration: underline;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  background-color: #2a2a2a;
  padding: 0;
  box-sizing: border-box;

}

.side {
  display: flex;
  width: 60%;
  background-color: transparent;
  height: 60px;
}

.header-box {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-weight: bold;
  background-color: transparent; /* Spotify green */
  cursor: pointer;
  transition: background-color 0.2s ease;
  /* No margin or padding horizontally to ensure boxes touch */
  padding: 0;
  border-right: 3px solid rgba(255, 255, 255, 1); /* subtle separator */
}

.header-box:last-child {
  border-right: none; /* Remove border from last item */
}

.header-box:hover {
  background-color: #1ed760; /* Lighter green on hover */
}
</style>