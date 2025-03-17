<script setup>
import { ref, inject } from 'vue'
import axios from 'axios'

const globalState = inject('globalState')

const signupName = ref('')
const signupEmail = ref('')
const signupPassword = ref('')
const loginEmail = ref('')
const loginPassword = ref('')

function login() {
    axios.post("http://localhost:8081/login", {
        email: loginEmail.value,
        password: loginPassword.value
    }).then(response => {
        console.log(response)
        globalState.token.value = response.data
        console.log(globalState.token.value)
    }).catch(error => {
        console.log(error) // gestionar errores
    })
}
</script>

<template>

<div class="loginBox">
    <div class="main">  	
		<input type="checkbox" id="chk" aria-hidden="true" checked>

			<div class="signup">
				<form>
					<label for="chk" aria-hidden="true">Sign up</label>
					<input type="text" name="txt" placeholder="Name" required>
					<input type="email" name="email" placeholder="Email" required>
					<input type="password" name="pswd" placeholder="Password" required>
					<button>Sign up</button>
				</form>
			</div>

			<div class="login">
				<form @submit.prevent="login">
					<label for="chk" aria-hidden="true">Login</label>
					<input type="email" name="email" placeholder="Email" required v-model="loginEmail">
					<input type="password" name="pswd" placeholder="Password" required v-model="loginPassword">
					<button>Login</button>
				</form>
			</div>
	</div>
</div>

</template>

<style>

.loginBox{
	margin: 0;
	padding: 0;
	display: flex;
	justify-content: center;
	align-items: center;
	min-height: 100vh;
	font-family: 'Jost', sans-serif;
}

.main{
	width: 350px;
	height: 500px;
	background: red;
	overflow: hidden;
	background: linear-gradient(to bottom, #0f0c29, #302b63, #24243e);
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
	background: #573b8a;
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
	background: #6d44b8;
}
.login{
	height: 460px;
	background: #eee;
	border-radius: 60% / 10%;
	transform: translateY(-180px);
	transition: .8s ease-in-out;
}
.login label{
	color: #573b8a;
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