<%@page contentType="text/html"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Firma digital</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
</head>

<body class="vh-100">
    <nav>
        <ul class="nav nav-pills nav-fill bg-light p-2">
            <li class="nav-item">
                <p class="h5 text-info">Crea tu documento y f�rmalo como propiedad de esta p�gina</p>
            </li>
        </ul>
    </nav>
    <div class="mt-2 d-flex align-items-center container">
        <div class="column w-100">
            <div class="row d-flex justify-content-center mt-5">
                <p class="h3 text-center">Hola</p>
            </div>
            <div class="row mt-5 d-flex align-items-stretch">
                <div class="col-md-12 col-lg-6 col-xl-4 mb-5 d-flex align-items-stretch">
                    <div class="card">
                        <div class="container d-flex justify-content-around mt-2">
                            <div class="row">
                                <div class="col-md-4 col-sm-12">
                                    <img class="rounded-circle" src="./img/key.png" width="100%">
                                </div>
                                <div class="col-md-8 col-sm-12 d-flex align-items-center">
                                    <p class="card-title h5">Crea tu par de llaves</p>
                                </div>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="container">
                                <form id="download-keys-form" method="POST" action="/SignDocument" enctype="multipart/form-data">
                                    <div class="form-row">
                                        <!--is-invalid is-valid-->
                                        <div class="col mb-3">
                                            <label for="download-key-passphrase">Contrase�a</label>
                                            <input type="text" class="form-control" placeholder="Contrase�a" id="download-key-passphrase" required>
                                        </div>
                                    </div>
                                    <div class="form-row d-flex justify-content-end">
                                        <button class="btn btn-primary mr-1" type="button" id="download-key-btn">Descargar llaves</button>
                                    </div>
                                </form>
                                <div class="row mt-3">
                                    <p class="text-secondary mr-2 text-justify">
                                        Una firma digital es un sello electr�nico cifrado sobre informaci�n digital como: 
                                        mensajes de email, macros, documentos electr�nicos, etc. 
                                        Una firma confirma que la informaci�n originada por el firmante no ha sido alterada.
                                    </p>
                                    <p class="text-secondary mr-2 text-justify">
                                        Para firmar, se requiere la llave privada que s�lo poseera el firmante 
                                        y la llave p�blica, que poseera cualquiera que quiera comprobar la firma
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-12 col-lg-6 col-xl-4 mb-5 d-flex align-items-stretch">
                    <div class="card">
                        <div class="container d-flex justify-content-around mt-2">
                            <div class="row">
                                <div class="col-md-4 col-sm-12">
                                    <img class="rounded-circle" src="./img/pen.jpg" width="100%">
                                </div>
                                <div class="col-md-8 col-sm-12 d-flex align-items-center">
                                    <p class="card-title h5">Crea y firma tu documento pdf</p>
                                </div>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="container">
                                <form id="sign-form" method="POST" action="/SignDocument" enctype="multipart/form-data">
                                    <div class="form-row">
                                        <!--is-invalid is-valid-->
                                        <div class="col-md-6 mb-3">
                                            <label for="name">Nombre</label>
                                            <input type="text" class="form-control" placeholder="Nombre" id="name" required>
                                            <div class="invalid-feedback">Tu nombre es incorrecto</div>
                                        </div>
                                        <div class="col-md-6 mb-3">
                                            <label for="lastname">Apellidos</label>
                                            <input type="text" class="form-control" placeholder="Apellidos" id="lastname" required>
                                            <div class="invalid-feedback">Revisa que tus apellidos est�n bien</div>
                                        </div>
                                    </div>
                                    <div class="form-row">
                                        <div class="col-md-9 mb-3">
                                            <label for="identifier">Boleta</label>
                                            <input type="text" class="form-control" placeholder="Boleta" id="identifier" required>
                                            <div class="invalid-feedback">Tu boleta parece estar mal</div>
                                        </div>
                                        <div class="col-md-3 mb-3">
                                            <label for="age">Edad</label>
                                            <input type="text" class="form-control" placeholder="Edad" id="age" required>
                                            <div class="invalid-feedback">Al parecer tu edad es incorrecta</div>
                                        </div>
                                    </div>
                                    <div class="form-row">
                                        <div class="col-md-12 mb-3">
                                           <label for="private-key-sign">Llave privada</label>
                                            <div class="custom-file">
                                                <input type="file" class="custom-file-input" id="private-key-sign" lang="es">
                                                <label class="custom-file-label" for="private-key-sign">Seleccionar Archivo</label>
                                            </div>
                                        </div>
                                        <div class="col-md-12 mb-3">
                                            <label for="passphrase-sign">Passphrase</label>
                                            <input type="text" class="form-control" placeholder="passphrase" id="passphrase-sign" required>
                                        </div>
                                        <div class="col-md-12 mb-3">
                                            <label for="sign-comment">Comentario</label>
                                            <textarea class="form-control" placeholder="Comentario" id="sign-comment"></textarea>
                                        </div>
                                    </div>
                                    <div class="form-row d-flex justify-content-end">
                                        <button class="btn btn-primary mr-1" type="button" id="sign-document-btn">Firmar archivo</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-12 col-lg-6 col-xl-4 mb-5 d-flex align-items-stretch">
                    <div class="card">
                        <div class="container d-flex justify-content-around mt-2">
                            <div class="row">
                                <div class="col-md-4 col-sm-12">
                                    <img class="rounded-circle" src="./img/verify.png" width="100%">
                                </div>
                                <div class="col-md-8 col-sm-12 d-flex align-items-center">
                                    <p class="card-title h5">Verifica la firma de tu documento pdf</p>
                                </div>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="container">
                                <form action="">
                                   <div class="form-row">
                                       <div class="col-md-12 mb-3">
                                            <label for="public-key-verify">Llave p�blica</label>
                                            <div class="custom-file">
                                                <input type="file" class="custom-file-input" id="public-key-verify" lang="es">
                                                <label class="custom-file-label" for="public-key-verify">Seleccionar Archivo</label>
                                            </div>
                                        </div>
                                        <div class="col-md-12 mb-3">
                                           <label for="pdf-file">archivo PDF</label>
                                            <div class="custom-file">
                                                <input type="file" class="custom-file-input" id="pdf-file" lang="es">
                                                <label class="custom-file-label" for="pdf-file">Seleccionar Archivo</label>
                                            </div>
                                        </div>
                                        <div class="col-md-12 mb-3">
                                           <label for="signature-file">firma digital</label>
                                            <div class="custom-file">
                                                <input type="file" class="custom-file-input" id="signature-file" lang="es">
                                                <label class="custom-file-label" for="signature-file">Seleccionar Archivo</label>
                                            </div>
                                        </div>
                                   </div>
                                   <div class="form-row d-flex justify-content-end">
                                        <button class="btn btn-primary mr-1" type="button" id="verify-sign-btn">verificar firma</button>
                                    </div>
                                </form>
                                <div class="container row mt-2">
                                    <p class="text-secondary mr-2">Resultado:</p>
                                    <p class="bg-dark text-white" id="entity"></p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="./js/ajax-request.js"></script>
    <script src="./js/pages/index.js"></script>
    <script src="./js/frameworks/jquery-3.4.1.slim.min.js"></script>
    <script src="./js/frameworks/popper.min.js"></script>
    <script src="./js/frameworks/bootstrap.min.js"></script>
</body></html>