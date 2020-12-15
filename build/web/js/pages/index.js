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
        input_file.nextElementSibling.innerHTML = input_file.value.split("\\").pop(-1) : alert("No se subió el archivo");
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
        form_data.append("passphrase", passphrase.value);
        
        let a = await fetch("SignDocument", {
            method: "POST",
            body: form_data
        });
        console.log(a);
    });
    download_key_passphrase.addEventListener("click", async ()=> {
        let content = document.getElementById('content').value;
        let request = new XMLHttpRequest();
        request.open('POST', '../server/', true);
        request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
        request.responseType = 'blob';

        request.onload = function() {
          // Only handle status code 200
            if(request.status === 200) {
                // Try to find out the filename from the content disposition `filename` value
                let disposition = request.getResponseHeader('content-disposition');
                let matches = /"([^"]*)"/.exec(disposition);
                let filename = (matches !== null && matches[1] ? matches[1] : 'pk.key');

                // The actual download
                let blob = new Blob([request.response], { type: 'application/pdf' });
                let link = document.createElement('a');
                link.href = window.URL.createObjectURL(blob);
                link.download = filename;

                //document.body.appendChild(link);
                link.click();
                //document.body.removeChild(link);
            } else {
                //alert("No se pudieron crear las llaves");
                console.log("No se pudieron crear las llaves");

            }
          };

          request.send('passphrase=' + download_key_passphrase.value);
      });
};

function downloadFile(urlToSend) {
     
 }