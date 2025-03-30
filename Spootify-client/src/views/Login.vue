<script setup>
import { ref, inject } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const globalState = inject('globalState')

const signupName = ref('')
const signupEmail = ref('')
const signupPassword = ref('')

let isArtist = false;
const showLogin = ref(true)

const loginEmail = ref('')
const loginPassword = ref('')
const errorMessage = ref('')



function login() {
	let url = "http://localhost:8081/login";
	if (isArtist) {
		url = "http://localhost:8081/artists/login";
	}

    axios.post(url, {
        email: loginEmail.value,
        password: loginPassword.value
    }).then(response => {
        console.log(response);
		console.log(response.data.token);
        globalState.token.value = response.data.token;
		globalState.userId.value = response.data.id;
		globalState.isArtist.value = isArtist;
		localStorage.setItem("token", response.data.token);
		localStorage.setItem("isArtist", isArtist);
		localStorage.setItem("id", response.data.id);
		if (isArtist) {
			router.push("/artists/dashboard")
		} else {
			router.push("/users/dashboard")
		}
    }).catch(error => {
        console.log(error) // gestionar errores
		if (error.status == 404) {
			if (isArtist) {
				errorMessage.value = "Artista no encontrado ¿Seguro que eres un artista?"
			} else {
				errorMessage.value = "Usuario no encontrado ¿Seguro que no eres un artista?"
			}
		} else if (error.status == 403) {
			errorMessage.value = "Contraseña incorrecta"
		} else {
			errorMessage.value = "Error desconocido"
		}
    })
}

function createAccount() {
	let url = "http://localhost:8081/users"
	if (isArtist) {
		url = "http://localhost:8081/artists"
	}
	axios.post(url, {
		name: signupName.value,
		email: signupEmail.value,
		password: signupPassword.value
	}).then(response => {
		console.log(response)
		showLogin.value = true
	}).catch(error => {
		console.log(error)
		if (error.status == 409) {
			errorMessage.value = "El email ya está en uso"
		} else {
			errorMessage.value = "Error desconocido"
		}
	})
}
</script>

<template>

<div class="loginBox">
    <div class="main">  	
		<input type="checkbox" id="chk" aria-hidden="true" v-model="showLogin" checked @click="errorMessage = ''">

			<div class="signup">
				<form @submit.prevent="createAccount">
					<label for="chk" id="signup" aria-hidden="true">Sign up</label>
					<p class="error">{{ errorMessage }}</p>
					<input type="text" name="txt" placeholder="Name" v-model="signupName" required>
					<input type="email" name="email" placeholder="Email" v-model="signupEmail" required>
					<input type="password" name="pswd" placeholder="Password" v-model="signupPassword" required>
					<div class="line">
						<input type="checkbox" id="artist" v-model="isArtist">
						<label for="artist" class="checkLabel">Soy un artista</label>
					</div>
					<button>Sign up</button>
				</form>
			</div>

			<div class="login">
				<form @submit.prevent="login">
					<label for="chk" aria-hidden="true">Login</label>
					<p class="error">{{ errorMessage }}</p>
					<input type="email" name="email" placeholder="Email" required v-model="loginEmail">
					<input type="password" name="pswd" placeholder="Password" required v-model="loginPassword">
					<div class="line">
						<input type="checkbox" id="artist" v-model="isArtist">
						<label for="artist" class="checkLabel">Soy un artista</label>
					</div>
					<button>Login</button>
				</form>
			</div>
	</div>
</div>

</template>

<style scoped>

.error {
	color: red;
	text-align: center;
	font-size: 0.8em;
	padding-right: 5em;
	padding-left: 5em;
}

#signup {
	margin-bottom: 1em;
}

.loginBox{
	margin: 0;
	padding: 0;
	display: flex;
	justify-content: center;
	align-items: center;
	font-family: 'Jost', sans-serif;
	border: 1px solid green;
	/* Add these lines to make it full height and centered */
    min-height: 100vh; /* Makes it take the full viewport height */
    width: 100%; /* Ensures it spans the full width */
    position: absolute; /* Takes it out of normal flow */
    top: 0; /* Position from the top edge */
    left: 0; /* Position from the left edge */
}

.main{
	width: 350px;
	height: 500px;
	background: red;
	overflow: hidden;
	background: linear-gradient(to bottom, #0c291a, rgb(28, 165, 76), #263e24);
	border-radius: 10px;
	box-shadow: 5px 20px 50px #000;
}
#chk{
	display: none;
}
.signup{
	position: relative;
	width:100%;
	height: 100%;
}
label{
	color: #fff;
	font-size: 2.3em;
	justify-content: center;
	display: flex;
	margin: 60px;
	font-weight: bold;
	cursor: pointer;
	transition: .5s ease-in-out;
}

.checkLabel {
	font-size: 1em;
	margin-top: 0;
	margin-left: 0em;
	display: inline;
}

#artist {
    display: inline-block; /* Ensure the checkbox is inline */
    vertical-align: middle; /* Align the checkbox vertically with the label */
	margin: 0;
	margin-right: 1em;
	padding: 0;
	width: auto;
}


.line {
	display: flex;
	justify-content: center;
	height: 1em;
}


input{
	width: 60%;
	height: 20px;
	background: #e0dede;
	justify-content: center;
	display: flex;
	margin: 20px auto;
	padding: 10px;
	border: none;
	outline: none;
	border-radius: 5px;
}

button{
	width: 60%;
	height: 40px;
	margin: 10px auto;
	justify-content: center;
	display: block;
	color: #fff;
	background: rgb(30, 215, 96);
	font-size: 1em;
	font-weight: bold;
	margin-top: 20px;
	outline: none;
	border: none;
	border-radius: 5px;
	transition: .2s ease-in;
	cursor: pointer;
}
button:hover{
	background: rgb(22, 164, 72);
}
.login{
	height: 460px;
	background: #eee;
	border-radius: 60% / 10%;
	transform: translateY(-180px);
	transition: .8s ease-in-out;
}
.login label{
	color: rgb(30, 215, 96);
	transform: scale(.6);
}

#chk:checked ~ .login{
	transform: translateY(-500px);
}
#chk:checked ~ .login label{
	transform: scale(1);	
}
#chk:checked ~ .signup label{
	transform: scale(.6);
}


</style>