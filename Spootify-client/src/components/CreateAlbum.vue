<template>
    <div id="contFormulario">
        <div id="titulo">
            <p>CREAR ÁLBUM</p>
        </div>
        <div id="campoNombre">
            <p id="labelNombre">Nombre:</p>
            <input id="inputNombre" type="text">
        </div>
    </div>
</template>

<style scoped>
#contFormulario {
    max-width: 100px;
    border: 3px solid green;
    border-radius: 10px;
}

#titulo {
    margin: 0 auto;
    text-align: center;
    border: 2px solid black;
    width: 300px;
}

#campoNombre {
    border: 2px solid blue;
    display: flex;
    align-items: center;
}

#labelNombre {
    border: 2px solid red;
    margin-left: 185px;
}

#inputNombre {
    border: 2px solid pink;
    max-width: 10px;
}

</style>