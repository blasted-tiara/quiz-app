let wsConnection = null;
let answeredCurrentQuestion = false;

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
                answeredCurrentQuestion = false;

                document.getElementById('answers').innerHTML = answers.join('');
                timeLimit = question.time;
                initTimer();
                startTimer();

                showSection("question-section");

                break;
            case FromServerMessage.timeUp:
            case FromServerMessage.finalResults:
                const parsedResults = JSON.parse(message);
                fillTableWithResults(parsedResults, 'results-table-body');
                showSection("results-section");
                break;
            case FromServerMessage.quizDetails:
                const quizDetails = JSON.parse(message);
                initQuizData(quizDetails);

                break;
        }
    }

    wsConnection.onclose = function () {
        console.log("WebSocket is closed now.");
    }
}

function initQuizData(quiz) {
    document.getElementById('quiz-title').innerText = quiz.title;
    document.getElementById('quiz-description').innerText = quiz.description;
    document.getElementById('quiz-image').src = constructQuizThumbnailURL(quiz);
}
 
function constructQuizThumbnailURL(quiz) {
    return `http://localhost:8080/api/images/${quiz.thumbnail || "default.jpg"}`;
}

function createPlayerName(player) {
    const name = player.name;
    const surname = player.surname;

    return `${name} ${surname}`;
}

function fillTableWithResults(results, tableBodyId) {
    let tableBody = document.getElementById(tableBodyId);
    tableBody.innerHTML = '';

    for (let result of results) {
        let row = `
          <tr>
            <td>${createPlayerName(result)}</td>
            <td>${result.score}</td>
          </tr>
        `;
        tableBody.innerHTML += row;
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

    if (!answeredCurrentQuestion) {
        const selectedAnswer = document.querySelector(".player-answer-item.selected");
        if (selectedAnswer) {
            const answerId = selectedAnswer.dataset.id;
            wsConnection.send(ToServerMessage.answer + ":" + answerId);
        }
        answeredCurrentQuestion = true;
        showSection("answer-sent");
    }
}

function selectAnswer(answer) {
    if (!answeredCurrentQuestion) {
        // Remove "selected" class from all answer elements
        const answers = document.getElementsByClassName("player-answer-item");
        for (let i = 0; i < answers.length; i++) {
            answers[i].classList.remove("selected");
        }

        // Add "selected" class to the clicked answer element
        answer.classList.add("selected");
    }
}

const ToServerMessage = {
    playerData: 'PLAYER_DATA',
    answer: 'ANSWER',
}

const FromServerMessage = {
    question: 'QUESTION',
    timeUp: 'TIME_UP',
    finalResults: 'FINAL_RESULTS',
    quizDetails: 'QUIZ_DETAILS',
}