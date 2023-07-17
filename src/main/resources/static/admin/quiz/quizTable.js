const quizEndpoint = 'http://localhost:8080/api/quiz/';

function createQuizzesTableRow(quiz) {
    let row = document.createElement('tr');

    let thumbnailCell = document.createElement('td');
    let img = document.createElement('img');
    img.src = constructVideoThumbnailURL(quiz);
    img.alt = "Video thumbnail";
    thumbnailCell.appendChild(img);

    let titleCell = document.createElement('td');
    titleCell.textContent = quiz.title;
    
    let descriptionCell = document.createElement('td');
    descriptionCell.textContent = quiz.description;

    let actionsCell = document.createElement("td");

    let editButton = document.createElement("button");
    editButton.className = "action-button";

    let editIcon = document.createElement("i");
    editIcon.className = "fas fa-edit";
    
    editButton.appendChild(editIcon).addEventListener('click', function (e) {
        e.preventDefault();

        // Fetch the current data for the quiz
        fetch(quizEndpoint + quiz.id)
            .then(response => response.json())
            .then(data => {
                let quiz = new Quiz(data);
                let modalContent = document.getElementById('edit-quiz-modal-content');
                modalContent.innerHTML = '';
                modalContent.appendChild(quiz.generateHTML());
                quiz.attachListeners();

                $("#image-picker-edit").change(function (event) {
                    console.log("Attached successfully");
                    readURL(this, "#image-preview-edit");
                });
        

                const editQuizForm = document.getElementById('edit-quiz-form');
                editQuizForm.addEventListener('submit', (e) => {
                    updateEntity(e, getFormDataEdit, quizEndpoint, "edit-quiz-modal", "edit-quiz-form");
                })
                
                // Show the modal
                showModal('edit-quiz-modal');
            });
    });

    let deleteButton = document.createElement("button");
    deleteButton.className = "action-button";

    let deleteIcon = document.createElement("i");
    deleteIcon.className = "fas fa-trash-alt";

    deleteButton.appendChild(deleteIcon);
    deleteButton.addEventListener('click', function (e) {
        e.preventDefault();
        
        document.getElementById('delete-user-modal').dataset.entityId = quiz.id;

        showModal('delete-user-modal');
    })

    let playButton = document.createElement("button");
    playButton.className = "action-button";
    playButton.addEventListener('click', function (e) {
        e.preventDefault();
        document.getElementById('play-quiz-modal').dataset.entityId = quiz.id;
        showModal('play-quiz-modal');
    });

    let playIcon = document.createElement("i");
    playIcon.className = "fas fa-play";

    playButton.appendChild(playIcon);
    playButton.addEventListener('click', function (e) {
        e.preventDefault();
    });

    actionsCell.appendChild(playButton);
    actionsCell.appendChild(editButton);
    actionsCell.appendChild(deleteButton);

    row.appendChild(thumbnailCell);
    row.appendChild(titleCell);
    row.appendChild(descriptionCell);
    row.appendChild(actionsCell);

    return row;
}

function constructVideoThumbnailURL(video) {
    return `http://localhost:8080/api/images/${video.thumbnail || "default.jpg"}`;
}
