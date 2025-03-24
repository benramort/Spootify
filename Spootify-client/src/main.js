// import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { globalState } from './globalState'

const app = createApp(App)

app.provide('globalState', globalState)

app.use(router)
app.mount('#app')

export function printDuration(seconds) {
    let minutes = Math.floor(seconds / 60);
    let remainingSeconds = seconds % 60;
    if (remainingSeconds < 10) {
        remainingSeconds = "0" + remainingSeconds;
    }
    return `${minutes}:${remainingSeconds}`;
}