let wsConnection = null;
let quizFinished = false;

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

function copyToClipboard(e) {
    e.preventDefault();
    // Get the pin number element
    const pinNumber = document.getElementById('pin');

    // Create a temporary input element
    const tempInput = document.createElement('input');
    tempInput.value = pinNumber.innerText;
    document.body.appendChild(tempInput);

    // Copy the pin number value to clipboard
    tempInput.select();
    document.execCommand('copy');
    document.body.removeChild(tempInput);

    // Alert the user that the pin has been copied
    alert('Pin copied to clipboard!');
}

function initQuizData() {
    // fetch quiz
    const urlParams = new URLSearchParams(window.location.search);
    const quizId = urlParams.get('quizId');
    fetch(`/api/quiz/${quizId}`, { "method": "GET" })
        .then(response => {
            const text = response.text();
            return text;
        })
        .then(quiz => {
            const parsedQuiz = JSON.parse(quiz);
            document.getElementById('quiz-title').innerText = parsedQuiz.title;
            document.getElementById('quiz-description').innerText = parsedQuiz.description;
            document.getElementById('quiz-image').src = constructQuizThumbnailURL(parsedQuiz);
        });
}

function constructQuizThumbnailURL(quiz) {
    return `http://localhost:8080/api/images/${quiz.thumbnail || "default.jpg"}`;
}

function startWebsocketConnection(pin) {
    wsConnection = new WebSocket(`ws://localhost:8080/admin/ws/quiz?pin=${pin}`);

    wsConnection.onopen = function () {
        initShowPinSection(pin);
        switchToSection('show-pin-section');
    }

    wsConnection.onmessage = function (e) {
        const data = decodeMessage(e.data);
        const messageType = data[0];
        const message = data[1];
        switch (messageType) {
            case fromHostMessages.numberOfPlayers:
                updateNumberOfPlayers(message);
                break;
            case fromHostMessages.quizStarted:
                switchToSection('control-quiz-section');
                break;
            case fromHostMessages.question:
                const parsedMessage = JSON.parse(message);
                const question = new Question(parsedMessage);
                document.getElementById('question-text').innerText = question.text;
                const answers = [];
                question.answers.forEach((answer, index) => {
                    if (answer.correct) {
                        answers.push(` <li class="answer correct-answer">${answer.text}</li>`)
                    } else {
                        answers.push(` <li class="answer">${answer.text}</li>`)
                    }
                });
                document.getElementById('answers').innerHTML = answers.join('');

                timeLimit = question.time;
                initTimer();
                startTimer();
                setShowResultsButtonDisabled(true)
                setNextQuestionBtnDisabled(true)
                break;
            case fromHostMessages.finalResults:
                quizFinished = true;
                const finalResults = JSON.parse(message);
                setShowResultsButtonDisabled(false)
                setNextQuestionBtnDisabled(true)
                fillTableWithResults(finalResults, 'final-results-table-body');
                break;
            case fromHostMessages.timeUp:
                const currentResults = JSON.parse(message);
                setShowResultsButtonDisabled(false)
                setNextQuestionBtnDisabled(false)
                fillTableWithResults(currentResults, 'current-results-table-body');
                break;
            case fromHostMessages.finalResultsAsXls:
                downloadExcelFile(message, 'results.xlsx');
                break;
            default:
                break;
        }
    }

    wsConnection.onclose = function () {
    }
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

function sentDownloadResultsRequest(e) {
    e.preventDefault();
    wsConnection.send(toHostMessages.getResultsAsXls);
}

function downloadExcelFile(base64Data, fileName) {
    const byteCharacters = atob(base64Data);
    const byteNumbers = new Array(byteCharacters.length);

    for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
    }

    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });

    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = fileName;
    link.click();

    URL.revokeObjectURL(link.href);
}

function createPlayerName(player) {
    const name = player.name;
    const surname = player.surname;

    return `${name} ${surname}`;
}

function showResultsModal(e) {
    e.preventDefault();

    if (quizFinished) {
        showModal('final-results-modal');
    } else {
        showModal('top-10-players-modal');
    }
}

function initShowPinSection(pin) {
    document.getElementById('pin').innerText = pin;
}

function startQuiz(e) {
    e.preventDefault();
    wsConnection.send(toHostMessages.startQuiz);
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

function setShowResultsButtonDisabled(disabled) {
    const showResultsButton = document.getElementById('show-results-btn');
    showResultsButton.disabled = disabled;
}

function setNextQuestionBtnDisabled(disabled) {
    const nextQuestionBtn = document.getElementById('next-question-btn');
    nextQuestionBtn.disabled = disabled;
}

function nextQuestion(e) {
    e.preventDefault();
    wsConnection.send(toHostMessages.nextQuestion);
}

const toHostMessages = {
    startQuiz: 'START_QUIZ',
    endQuiz: 'END_QUIZ',
    getCurrentQuestion: 'GET_CURRENT_QUESTION',
    startCurrentQuestion: "START_CURRENT_QUESTION",
    nextQuestion: 'NEXT_QUESTION',
    getResultsAsXls: 'GET_RESULTS_AS_XLS',
}

const fromHostMessages = {
    quizStarted: 'QUIZ_STARTED',
    question: 'QUESTION',
    numberOfPlayers: 'NUMBER_OF_PLAYERS',
    timeUp: 'TIME_UP',
    finalResults: 'FINAL_RESULTS',
    finalResultsAsXls: 'FINAL_RESULTS_AS_XLS',
}