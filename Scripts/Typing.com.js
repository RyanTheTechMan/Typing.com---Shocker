//It is recommended to use a JS Injector like Scripty: https://chrome.google.com/webstore/detail/scripty-javascript-inject/milkbiaeapddfnpenedfgbfdacpbcbam

var shockingClient = new WebSocket('ws://127.0.0.1:4444');

shockingClient.onopen = function () {
    console.log('Connected!');
};

shockingClient.onerror = function (error) {
    console.log('WebSocket Error ' + error);
};

shockingClient.onmessage = function (e) {
    console.log('Server: ' + e.data);
};

var shockingLoop = window.setInterval(function(){
    var html = document.querySelector("#app > div > div:nth-child(2) > div > div > div.structure > div.structure-content > div.cell.js-keyboard-holder > div > div")
    if (html != null && html.outerHTML.toString().includes("is-wrong")){
        shockingClient.send('shock');
    }
    html = document.querySelector("#app > div > div:nth-child(1) > div > div > div.structure > div.structure-content > div.cell.js-keyboard-holder")
    if (html != null && html.outerHTML.toString().includes("is-wrong")){
        shockingClient.send('shock');
    }
}, 100);