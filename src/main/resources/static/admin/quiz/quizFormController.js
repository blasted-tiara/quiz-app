function getFormDataCreate() {
    let formData = new FormData();

    const title = document.getElementById('title-create').value;
    const description = document.getElementById('description-create').value;
    const thumbnailFileInput = document.getElementById('image-picker-create');

    formData.append('title', title);
    formData.append('description', description);
    if (thumbnailFileInput.files.length > 0) { // make sure a file was selected
        formData.append('thumbnailFile', thumbnailFileInput.files[0]);
    }

    return formData;
}

function getFormDataEdit() {
    let formData = new FormData();

    const quiz = getQuizData();
    const thumbnailFileInput = document.getElementById('image-picker-edit');

    formData.append('quiz', JSON.stringify(quiz));
    if (thumbnailFileInput.files.length > 0) { // make sure a file was selected
        formData.append('thumbnailFile', thumbnailFileInput.files[0]);
    }

    return formData;
}

function setFormData(quiz) {
    document.getElementById('title-edit').value = quiz.title;
    document.getElementById('description-edit').value = quiz.description;
}

function getQuizData() {
    // Get the input values from the HTML components
    const titleInput = document.getElementById('title-edit');
    const descriptionInput = document.getElementById('description-edit');
    const quizId = parseInt(document.getElementById('edit-quiz-form').dataset.entityId);
    const thumbnailUrl = document.getElementById('image-preview-edit').src;
    const thumnailUrlParts = thumbnailUrl.split('/');
    const thumbnail = thumnailUrlParts[thumnailUrlParts.length - 1];

    // Create the Quiz object with the gathered data
    const quiz = {
        id: quizId,
        title: titleInput.value,
        description: descriptionInput.value,
        // thumbnail: thumbnail,
        questions: []
    };

    // Get the question cards
    const questionCards = document.getElementsByClassName('question-card');

    // Loop through each question card
    for (let i = 0; i < questionCards.length; i++) {
        const questionId = parseInt(questionCards[i].dataset.questionId);
        const questionCard = questionCards[i];

        // Get the question input value
        const questionInput = questionCard.querySelector('.mdc-text-field__input');
        const questionText = questionInput.value;

        // Get the points and time inputs
        const pointsInput = questionCard.querySelector('.scaled-left-input .mdc-text-field__input');
        const timeInput = questionCard.querySelector('.scaled-right-input .mdc-text-field__input');

        // Create the Question object with the gathered data
        const question = {
            id: questionId,
            text: questionText,
            points: parseInt(pointsInput.value),
            time: parseInt(timeInput.value),
            answers: []
        };

        // Get the answer items for the current question card
        const answerItems = questionCard.getElementsByClassName('answer-item');

        // Loop through each answer item
        for (let j = 0; j < answerItems.length; j++) {
            const answerItem = answerItems[j];

            // Get the answer checkbox and input
            const checkbox = answerItem.querySelector('.answer-checkbox');
            const answerInput = answerItem.querySelector('.answer-input');
            const answerId = parseInt(answerItem.dataset.answerId);

            // Create the Answer object with the gathered data
            const answer = {
                id: answerId,
                text: answerInput.value,
                correct: checkbox.checked
            };

            // Add the answer to the current question
            question.answers.push(answer);
        }

        // Add the question to the quiz
        quiz.questions.push(question);
    }

    // Return the gathered quiz data
    return quiz;
}