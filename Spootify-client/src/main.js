// import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { globalState } from './globalState'

const app = createApp(App)

app.provide('globalState', globalState)

app.use(router)
app.mount('#app')
