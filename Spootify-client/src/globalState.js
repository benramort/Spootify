import { ref, watch } from 'vue'


export const globalState = { //Guardar en el localStorage
    token: ref(localStorage.getItem('token')),
    isArtist: ref(localStorage.getItem('isArtist'))
}

// if (import.meta.env.DEV) {
//     watch(() => globalState.token.value, (newVal, oldVal) => {
//       console.log(`Token changed from ${oldVal} to ${newVal}`);
//     });
// }