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

    wsConnection.onmessage = function (e) {
        const data = decodeMessage(e.data);
        const messageType = data[0];
        const message = data[1];
        
        switch (messageType) {
            case FromServerMessage.question:
                const parsedMessage = JSON.parse(message);
                const question = new Question(parsedMessage);

                document.getElementById('question-text').innerText = question.text;

                const answers = [];
                question.answers.forEach((answer) => {
                    answers.push(`
                        <li class="player-answer-item" onclick="selectAnswer(this)" data-id="${answer.id}">
                            <div class="player-answer">
                                <span class="player-answer-text">${answer.text}</span>
                            </div>
                        </li>
                    `)
                });

                document.getElementById('answers').innerHTML = answers.join('');
                
                showSection("question-section");

                break;
            case FromServerMessage.timeUp:
                break;
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

function submitAnswer(e) {
    e.preventDefault();

    const selectedAnswer = document.querySelector(".player-answer-item.selected");
    if (selectedAnswer) {
        const answerId = selectedAnswer.dataset.id;
        wsConnection.send(ToServerMessage.answer + ":" + answerId);
    }
}

function selectAnswer(answer) {
    // Remove "selected" class from all answer elements
    const answers = document.getElementsByClassName("player-answer-item");
    for (let i = 0; i < answers.length; i++) {
      answers[i].classList.remove("selected");
    }
  
    // Add "selected" class to the clicked answer element
    answer.classList.add("selected");
  }

const ToServerMessage = {
    playerData: 'PLAYER_DATA',
    answer: 'ANSWER',
}

const FromServerMessage = {
    question: 'QUESTION',
    timeUp: 'TIME_UP',
}