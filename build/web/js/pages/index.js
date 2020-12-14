//forms
const sign_form = document.querySelector("#sign-form");
//buttons
const sign_document_btn = document.querySelector("#sign-document-btn");
const verify_sign_btn = document.querySelector("#verify-sign-btn");
//sign inputs
const name = document.querySelector("#name");
const lastname = document.querySelector("#lastname");
const age = document.querySelector("#age");
const identifier = document.querySelector("#identifier"); // boleta
const private_key = document.querySelector("#private-key");
const passphrase = document.querySelector("#passphrase");

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
};

/*const document_data = {
            name: name.value,
            lastname: lastname.value,
            identifier: identifier.value,
            private_key: private_key.files[0],
            passphrase: passphrase.value
        };*/