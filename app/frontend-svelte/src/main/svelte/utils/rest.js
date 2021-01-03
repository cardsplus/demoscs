export async function fetchHtml(htmlUrl) {
    return fetch(BACKEND_URL + htmlUrl, {
        method: 'GET',
        headers: {
            'Authorization': 'Basic ' + btoa(BACKEND_USER),
            'Accept': 'text/html'
        }
    })
    .then(res => {
        console.log(res.status);
        if (res.ok) return res;
        throw Error(htmlUrl + ' failed with ' + res.status);        
    })
    .catch(err => {
        console.log(err);
        throw err;
    });
}

