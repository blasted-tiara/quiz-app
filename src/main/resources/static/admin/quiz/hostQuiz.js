let wsConnection = null;

function getQuizPin(e) {
    e.preventDefault();

    const urlParams = new URLSearchParams(window.location.search);
    const quizId = urlParams.get('quizId');
    fetch(`/api/quiz/${quizId}/generatePin`, { "method": "POST" })
        .then(response => {
            const text = response.text();
            return text;
        })
        .then(pin => {
            startWebsocketConnection(pin);
        });
        
}

function startWebsocketConnection(pin) {
    wsConnection = new WebSocket(`ws://localhost:8080/admin/ws/quiz?pin=${pin}`);
    
    wsConnection.onopen = function () {
        initShowPinSection(pin);
        switchToSection('show-pin-section');
    }

    wsConnection.onmessage = function (e) {
        const message = e.data;
        if (message.startsWith(hostMessages.numberOfPlayers)) {
            const numberOfPlayers = message.split(':')[1];
            updateNumberOfPlayers(numberOfPlayers);
        }
    }

    wsConnection.onclose = function () {
    }
}

function initShowPinSection(pin) {
    document.getElementById('pin').innerText = pin;
}

function switchToSection(sectionId) {
    const sections = document.getElementsByClassName('quiz-section');
    for (let i = 0; i < sections.length; i++) {
        sections[i].classList.add('hidden');
    }

    document.getElementById(sectionId).classList.remove('hidden');
}

function updateNumberOfPlayers(numberOfPlayers) {
    document.getElementById('number-of-players').innerText = numberOfPlayers;
}

const hostMessages = {
    startQuiz: 'START_QUIZ',
    endQuiz: 'END_QUIZ',
    nextQuestion: 'NEXT_QUESTION',
    numberOfPlayers: 'NUMBER_OF_PLAYERS'
}