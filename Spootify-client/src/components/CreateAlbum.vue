<template>
    <div id="contFormulario">
        <div id="titulo">
            <p id="crearAlbum">CREAR ÁLBUM</p>
        </div>
        <div id="campoNombre">
            <label for="nombre" id="labelNombre">Nombre:</label>
            <input id="inputNombre" type="text" placeholder="Name">
        </div>
        <div id="buttons">
            <button id="createButton">Create</button>
            <button id="cancelButton">Cancel</button>
        </div>
    </div>
</template>

<style scoped>
#contFormulario {
    max-width: 300px;
    border: 3px solid rgb(28, 165, 76);
    border-radius: 10px;
    margin: 0 auto;
}

#titulo {
    margin: 0 auto;
    text-align: center;
    width: 300px;
    height: 50px;
    border-bottom: 2px solid rgb(28, 165, 76);
    background: linear-gradient(to bottom, #0c291a, rgb(28, 165, 76));
    border-radius: 6px 6px 0 0;
    color: white;
    font-weight: bold;
}

#campoNombre {
    display: flex;
    align-items: center;
    margin: 0 auto;
    margin-top: 20px;
    margin-bottom: 20px;
}

#labelNombre {
    margin-left: 50px;
}

#inputNombre {
    background-color: lightgray;
    max-width: 100px;
    margin-left: 20px;
    border-radius: 5px;
}

#crearAlbum {
    margin-top: 0px;
    padding-top: 15px;
    margin: 0 auto;
}

#buttons {
    display: flex;
    justify-content: right;
    margin-bottom: 10px;
    margin-right: 10px;
    margin-top: 30px;
}

#createButton {
    background-color: rgb(30, 215, 96);
    color: black;
    border-radius: 5px;
    border-color: rgb(30, 215, 96);
    margin-right: 10px;
    width: 70px;
    height: 25px;
}

#cancelButton {
    background-color: lightgray;
    color: black;
    border-radius: 5px;
    width: 70px;
    height: 25px;
}


</style>