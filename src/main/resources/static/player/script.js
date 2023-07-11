let wsConnection = null;

function submitPin(e) {
    e.preventDefault();
    const pin = getPin();
    joinQuiz(pin);
}

function joinQuiz(pin) {
    wsConnection = new WebSocket(`ws://localhost:8080/ws/quiz?pin=${pin}`);
    
    wsConnection.onopen = function () {
        showSection("info-form-section");
    }

    wsConnection.onmessage = function (evt) {
        const data = decodeMessage(e.data);
        const messageType = data[0];
        const message = data[1];
        
        switch (messageType) {

        }
    }

    wsConnection.onclose = function () {
        console.log("WebSocket is closed now.");
    }
}

function getPin() {
    return document.getElementById("pin-input").value;
}

function sendAnswer(answer) {
    wsConnection.send(answer);
}

function showSection(sectionId) {
    document.getElementById("content").querySelectorAll("section").forEach(section => {
        section.style.display = "none";
    });
    document.getElementById(sectionId).style.display = "block";
}

function getUserInfo() {
    const name = document.getElementById("name-input").value;
    const email = document.getElementById("surname-input").value;
    return name + ":" + email;
}

function submitUserInfo(e) {
    e.preventDefault();
    const userInfo = getUserInfo();
    wsConnection.send(encodeMessage(ToServerMessage.playerData, userInfo));
    showSection("waiting-host-section");
}

const ToServerMessage = {
    playerData: 'PLAYER_DATA',
}

const FromServerMessage = {
    question: 'QUESTION',
    timeUp: 'TIME_UP',
}