const quizEndpoint = 'http://localhost:8080/api/quiz/';

function createQuizzesTableRow(quiz) {
    let row = document.createElement('tr');

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
            .then(user => {
                /*
                const mdcTextFieldElement = document.getElementById('edit-username-component');
                const nativeInputField = mdcTextFieldElement.querySelector('input');
                nativeInputField.value = user.username;
                
                const mdcTextField = mdc.textField.MDCTextField.attachTo(mdcTextFieldElement);
                mdcTextField.foundation.setValue(user.username);
                
                document.getElementById('edit-user-form').dataset.userId = user.id;
                
                // Show the modal
                showModal('edit-user-modal');
                */
            });
    });

    let deleteButton = document.createElement("button");
    deleteButton.className = "action-button";

    let deleteIcon = document.createElement("i");
    deleteIcon.className = "fas fa-trash-alt";

    deleteButton.appendChild(deleteIcon);
    deleteButton.addEventListener('click', function (e) {
        e.preventDefault();
        
        document.getElementById('delete-user-modal').dataset.userId = quiz.id;

        showModal('delete-user-modal');
    })

    actionsCell.appendChild(editButton);
    actionsCell.appendChild(deleteButton);

    row.appendChild(titleCell);
    row.appendChild(descriptionCell);
    row.appendChild(actionsCell);

    return row;
}
