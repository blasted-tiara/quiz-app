let wsConnection = null;

function submitPin(e) {
    e.preventDefault();
    const pin = getPin();
    joinQuiz(pin);
}

function joinQuiz(pin) {
    wsConnection = new WebSocket(`ws://localhost:8080/ws/quiz?pin=${pin}`);
    
    wsConnection.onopen = function () {
        showSection("waiting-host-section");

    }

    wsConnection.onmessage = function (evt) {
        console.log("Message is received: " + evt.data);
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
