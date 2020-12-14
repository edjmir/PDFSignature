const ajaxRequest = (method, url, json_data) => {
    return new Promise((resolve, reject) => {
        const req = new XMLHttpRequest();
        req.onreadystatechange = () => {
            if (req.readyState === 4)
                return req.status === 200 ? resolve (req.responseText) : reject (req.status);
            else 
                return reject (req.readyState);
        };
        req.open(method, url, true);
        req.setRequestHeader("Content-Type", "application/json;charset=utf-8");
        req.send(JSON.stringify(json_data));
    });
};

const ajaxUploadRequest = (method, url, form_data)=> {
    return new Promise((resolve, reject) =>{
        const req = new XMLHttpRequest();
        req.onreadystatechange = () => {
            if (req.readyState === 4)
                return req.status === 200 ? resolve(req.responseText) : reject (req.status);
            else
                return reject (req.readyState);
        };
        req.open(method, url, true);
        req.setRequestHeader("Content-Type", "multipart/form-data");
        req.send(form_data);
    });
};