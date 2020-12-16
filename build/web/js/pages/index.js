//forms
const sign_form = document.querySelector("#sign-form");
//buttons
const sign_document_btn = document.querySelector("#sign-document-btn");
const verify_sign_btn = document.querySelector("#verify-sign-btn");
const download_key_btn = document.querySelector("#download-key-btn");
//sign inputs
const name = document.querySelector("#name");
const lastname = document.querySelector("#lastname");
const age = document.querySelector("#age");
const identifier = document.querySelector("#identifier"); // boleta
const private_key = document.querySelector("#private-key");
const passphrase = document.querySelector("#passphrase");
const download_key_passphrase = document.querySelector("#download-key-passphrase");

window.addEventListener("load", () => {
    addListeners();
});

const addFile = (input_file) => {
    input_file.value ?
        input_file.nextElementSibling.innerHTML = input_file.value.split("\\").pop(-1) : "Seleccionar archivo";
};

const addListeners = () => {
    private_key.addEventListener("change", function () {
        addFile(this);
    });
    sign_document_btn.addEventListener("click", async ()=> {
        let form_data = new FormData();
        form_data.append("file", private_key.files[0]);
        form_data.append("name", name.value);
        form_data.append("lastname", lastname.value);
        form_data.append("age", age.value);
        form_data.append("identifier", identifier.value);
        form_data.append("passphrase", passphrase.value.length ? passphrase.value : null);
        
        let res = await fetch("SignDocument", {
            method: "POST",
            body: form_data
        }).then( res => res.blob() )
        .then( blob => {
            if(blob.size === 0){
                console.log("Error de los datos");
                return;
            }
            var file = window.URL.createObjectURL(blob);
            let file_name = "MyPdf.pdf";
            //window.location.assign(file);
            
            let link = document.createElement('a');
            link.href = file;
            
            link.download = file_name;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            
        }).catch((error) => {
            console.error('Error:', error);
        });
        
        console.log(res);
    });
    download_key_btn.addEventListener("click", async ()=> {
        const data = `passphrase=${download_key_passphrase.value.length ? download_key_passphrase.value : null}`;
        const res = downloadFile(
            "POST",
            "DownloadPrivateKey",
            'application/x-www-form-urlencoded;charset=UTF-8',
            data,
            "application/octet-stream",
            "pk.key"
        );
        if(res === -1){
            console.log("No se pudieron obtener las llaves");
        }
    });
};

const downloadFile = (method, url, content_type, data, blob_type, file_def_name) => {
    let request = new XMLHttpRequest();
    request.open(method, url, true);
    request.setRequestHeader('Content-Type', content_type);
    request.responseType = 'blob';

    request.onload = () => {
      // Only handle status code 200
        if(request.status === 200) {
            // Try to find out the filename from the content disposition `filename` value
            let disposition = request.getResponseHeader('content-disposition');
            let matches = /"([^"]*)"/.exec(disposition);
            let filename = (matches !== null && matches[1] ? matches[1] : file_def_name);

            // The actual download
            let blob = new Blob([request.response], { type: blob_type });
            let link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = filename;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        } else {
            return -1;
        }
    };
    request.send(data);
 };