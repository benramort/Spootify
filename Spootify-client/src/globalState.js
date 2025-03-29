import { ref, watch } from 'vue'


export const globalState = { //Guardar en el localStorage
    token: ref(parseInt(localStorage.getItem('token'))),
    userId: ref(parseInt(localStorage.getItem('id'))),
    isArtist: ref(localStorage.getItem('isArtist') == 'true')
}

if (import.meta.env.DEV) {
    watch(() => globalState.token.value, (newVal, oldVal) => {
      console.log(`Token changed from ${oldVal} to ${newVal}`);
    });
}