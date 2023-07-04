class Quiz {
    constructor(quiz) {
        this.id = quiz.id;
        this.title = quiz.title;
        this.description = quiz.description;
        this.thumbnail = quiz.thumbnail;
        this.questions = quiz.questions.map((question) => {
            return new Question(question);
        });
    }

    generateHTML() {
        const componentFactory = new ComponentFactory();
        // Create the section element
        const section = document.createElement('section');
        section.classList.add('user-form');

        // Create the form element
        const form = document.createElement('form');
        form.setAttribute('id', 'edit-quiz-form');
        form.dataset.entityId = this.id;

        // Create the first pane div
        const paneDiv = document.createElement('div');
        paneDiv.classList.add('pane');

        // Create the image picker
        const imagePickerLabel = document.createElement('label');
        imagePickerLabel.setAttribute('for', 'image-picker-edit');

        const imagePreview = document.createElement('img');
        imagePreview.setAttribute('id', 'image-preview-edit');
        imagePreview.classList.add('image-preview');
        imagePreview.setAttribute('src', this.constructThumbnailURL());
        imagePreview.setAttribute('alt', 'your image');

        imagePickerLabel.appendChild(imagePreview);

        const imagePicker = document.createElement('input');
        imagePicker.setAttribute('id', 'image-picker-edit');
        imagePicker.classList.add('image-picker');
        imagePicker.setAttribute('type', 'file');
        imagePicker.setAttribute('accept', 'image/*');

        const titleLabel = componentFactory.generateInputComponent('Title', this.title, 'title-edit', 'text', true);
        const descriptionLabel = componentFactory.generateInputComponent('Description', this.description, 'description-edit', 'text', true);

        // Append the image picker, title input, and description input to the pane div
        paneDiv.appendChild(imagePickerLabel);
        paneDiv.appendChild(imagePicker);
        paneDiv.appendChild(titleLabel);
        paneDiv.appendChild(descriptionLabel);

        // Append the pane div to the form
        form.appendChild(paneDiv);

        // Create the second div
        const secondDiv = document.createElement('div');

        // Create the questions section div
        const questionsSection = document.createElement('div');
        questionsSection.setAttribute('id', 'questions-section');

        // Generate question components and append to the questions section
        this.questions.forEach((question) => {
            const questionComponent = question.generateHTML();
            questionsSection.appendChild(questionComponent);
        });

        // Create the add question button
        const addQuestionButton = document.createElement('button');
        addQuestionButton.setAttribute('id', 'add-question');
        addQuestionButton.classList.add('add-question-button', 'symbol-button');
        addQuestionButton.innerHTML = '<i class="fas fa-plus-circle symbol"></i>';
        // add event on click
        addQuestionButton.addEventListener('click', (e) => {
            e.preventDefault();

            const question = new Question();
            const questionComponent = question.generateHTML();
            questionsSection.appendChild(questionComponent);
            this.attachListeners();
        });

        // Append the questions section and add question button to the second div
        secondDiv.appendChild(questionsSection);
        secondDiv.appendChild(addQuestionButton);

        // Append the form and second div to the section
        section.appendChild(form);
        section.appendChild(secondDiv);

        return section;
    }

    attachListeners() {
        const textFields = document.querySelectorAll('.mdc-text-field');
        textFields.forEach((textField) => {
            mdc.textField.MDCTextField.attachTo(textField);
        });

    }
    
    constructThumbnailURL() {
        return `http://localhost:8080/api/images/${this.thumbnail || "default.jpg"}`;
    }
}

class Question {
    constructor(question) {
        if (question) {
            this.id = question.id;
            this.text = question.text;
            this.time = question.time;
            this.points = question.points;
            this.questionOrder = question.questionOrder;
            this.answers = question.answers.map((answer) => {
                return new Answer(answer);
            });
        } else {
            this.id = null;
            this.text = '';
            this.time = 10;
            this.points = 1;
            this.answers = [];
        }
    }
    
    generateHTML() {
        const componentFactory = new ComponentFactory();

        // Create the question card
        const questionCard = document.createElement('div');
        questionCard.classList.add('question-card');
        questionCard.dataset.questionId = this.id;

        // Create the grab button
        const grabButton = document.createElement('div');
        grabButton.classList.add('grab-button');
        grabButton.setAttribute('draggable', 'true');

        const grabIcon = document.createElement('i');
        grabIcon.classList.add('fas', 'fa-grip-vertical');

        grabButton.appendChild(grabIcon);

        // Create the close button
        const closeButton = document.createElement('button');
        closeButton.classList.add('round-button', 'close-button');
        // add event on click
        closeButton.addEventListener('click', (e) => {
            e.preventDefault();

            questionCard.remove();
        });

        const closeIcon = document.createElement('i');
        closeIcon.classList.add('fas', 'fa-times');

        closeButton.appendChild(closeIcon);

        // Create the question info container
        const questionInfo = document.createElement('div');
        questionInfo.classList.add('question-info');

        const questionTextInput = componentFactory.generateTextareaComponent('Question Text', this.text, 'question-text-input-edit');
        questionInfo.appendChild(questionTextInput);

        // Create the question details container
        const questionDetails = document.createElement('div');
        questionDetails.classList.add('question-details');

        const pointsLabel = componentFactory.generateInputComponent('Points', this.points, 'points-input-edit', 'number', true, ['scaled-left-input']);
        const timeLabel = componentFactory.generateInputComponent('Time', this.time, 'time-input-edit', 'number', true, ['scaled-right-input']);

        // Create the answer list
        const answerList = document.createElement('ul');
        answerList.classList.add('answer-list');
        answerList.addEventListener('focus', (e) => {
            const answerInputs = answerList.querySelectorAll('.answer-input');
            const lastAnswerInput = answerInputs[answerInputs.length - 1];
          
            if (e.target === lastAnswerInput && lastAnswerInput.value === '') {
              // Append a new empty Answer component
              answerList.appendChild(new Answer());
            }
        });

        // Generate answer components and append to the answer list
        this.answers.forEach((answer) => {
            const answerComponent = answer.generateHTML();
            answerList.appendChild(answerComponent);
        });

        const addAnswerDiv = document.createElement('div');
        addAnswerDiv.classList.add('add-answer');

        const addAnswerButton = document.createElement('button');
        addAnswerButton.classList.add('add-answer-button');

        const plusIcon = document.createElement('i');
        plusIcon.classList.add('fa', 'fa-plus-circle');
        plusIcon.setAttribute('aria-hidden', 'true');

        addAnswerButton.appendChild(plusIcon);
        addAnswerDiv.appendChild(addAnswerButton);
        addAnswerButton.addEventListener('click', (e) => {
            e.preventDefault();

            const answer = new Answer();
            const answerComponent = answer.generateHTML();
            answerList.appendChild(answerComponent);
        });

        // Append the grab button, close button, question info, and answer list to the question card
        questionCard.appendChild(grabButton);
        questionCard.appendChild(closeButton);
        questionCard.appendChild(questionInfo);
        questionCard.appendChild(answerList);
        questionCard.appendChild(addAnswerDiv);

        // Append the question text input, question details to the question info
        questionInfo.appendChild(questionTextInput);
        questionInfo.appendChild(questionDetails);

        // Append the points input field, time input field to the question details
        questionDetails.appendChild(pointsLabel);
        questionDetails.appendChild(timeLabel);

        return questionCard;
    }
}

class Answer {
    constructor(answer) {
        if (answer) {
            this.id = answer.id;
            this.text = answer.text;
            this.correct = answer.correct;
        } else {
            this.id = null;
            this.text = '';
            this.correct = false;
        }
    }

    generateHTML() {
        const li = document.createElement('li');
        li.classList.add('answer-item');
        li.dataset.answerId = this.id;

        const label = document.createElement('label');
        label.classList.add('answer-label');

        const checkbox = document.createElement('input');
        checkbox.setAttribute('type', 'checkbox');
        checkbox.classList.add('answer-checkbox');
        checkbox.checked = this.correct;

        const input = document.createElement('input');
        input.setAttribute('type', 'text');
        input.classList.add('answer-input');
        input.setAttribute('placeholder', 'Answer Text');
        input.value = this.text;

        const button = document.createElement('button');
        button.classList.add('round-button', 'remove-answer-button');
        button.addEventListener('click', (e) => {
            e.preventDefault();

            li.remove();
        });

        const icon = document.createElement('i');
        icon.classList.add('fa', 'fa-trash');
        icon.setAttribute('aria-hidden', 'true');

        button.appendChild(icon);

        label.appendChild(checkbox);
        label.appendChild(input);
        label.appendChild(button);

        li.appendChild(label);

        return li;
    }
}

class ComponentFactory {
    generateInputComponent(label, value, id, type, required, styles = []) {
        const titleLabel = document.createElement('label');
        titleLabel.classList.add(...styles ,'mdc-text-field', 'mdc-text-field--outlined');

        const notchedOutline = document.createElement('span');
        notchedOutline.classList.add('mdc-notched-outline');

        const leadingOutline = document.createElement('span');
        leadingOutline.classList.add('mdc-notched-outline__leading');

        const notchOutline = document.createElement('span');
        notchOutline.classList.add('mdc-notched-outline__notch');

        const floatingLabel = document.createElement('span');
        floatingLabel.classList.add('mdc-floating-label');
        floatingLabel.setAttribute('id', 'my-label-id');
        floatingLabel.textContent = label;

        const trailingOutline = document.createElement('span');
        trailingOutline.classList.add('mdc-notched-outline__trailing');

        notchOutline.appendChild(floatingLabel);
        notchedOutline.appendChild(leadingOutline);
        notchedOutline.appendChild(notchOutline);
        notchedOutline.appendChild(trailingOutline);

        const input = document.createElement('input');
        input.setAttribute('id', id);
        input.setAttribute('type', type);
        input.classList.add('mdc-text-field__input');
        input.setAttribute('aria-labelledby', 'my-label-id');
        input.setAttribute('required', required);
        input.value = value;

        titleLabel.appendChild(notchedOutline);
        titleLabel.appendChild(input);

        return titleLabel;
    }

    generateTextareaComponent(labelText, text, id) {
        const label = document.createElement('label');
        label.classList.add('mdc-text-field', 'mdc-text-field--outlined', 'mdc-text-field--textarea', 'mdc-text-field--no-label', 'full-width');

        const notchedOutline = document.createElement('span');
        notchedOutline.classList.add('mdc-notched-outline');

        const leadingOutline = document.createElement('span');
        leadingOutline.classList.add('mdc-notched-outline__leading');

        const notchOutline = document.createElement('span');
        notchOutline.classList.add('mdc-notched-outline__notch');

        const floatingLabel = document.createElement('span');
        floatingLabel.classList.add('mdc-floating-label');
        floatingLabel.setAttribute('id', 'my-label-id');
        floatingLabel.textContent = labelText;

        const trailingOutline = document.createElement('span');
        trailingOutline.classList.add('mdc-notched-outline__trailing');

        const textareaContainer = document.createElement('span');
        textareaContainer.classList.add('full-width');

        const textarea = document.createElement('textarea');
        textarea.classList.add('mdc-text-field__input');
        textarea.setAttribute('rows', '4');
        textarea.setAttribute('aria-label', 'Label');
        textarea.setAttribute('id', id);
        textarea.value = text;

        notchOutline.appendChild(floatingLabel);
        notchedOutline.appendChild(leadingOutline);
        notchedOutline.appendChild(notchOutline);
        notchedOutline.appendChild(trailingOutline);
        textareaContainer.appendChild(textarea);
        label.appendChild(notchedOutline);
        label.appendChild(textareaContainer);

        return label;
    }
}
