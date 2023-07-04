function createEntity(e, getFormData, endpoint, modalId, contentType) {
    e.preventDefault();
    
    // Get the new values from the form. If json, then it needs to be stringified
    let formData = getFormData();

    let payload = {
        method: 'POST',
        body: formData,
    }
    
    if (contentType) {
        payload.headers = {
            'Content-Type': contentType
        }
    }

    // Send POST request
    fetch(endpoint, payload)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Something went wrong');
            }
        })
        .then(data => {
            console.log('Entity successfully created', data)
            // Close the modal
            closeModal(modalId);
            location.reload();
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}

function updateEntity(e, getFormData, endpoint, modalId, formId, contentType) {
    e.preventDefault();
    
    let formData = getFormData();

    const quizId = document.getElementById(formId).dataset.entityId;

    let payload = {
        method: 'PUT',
        body: formData,
    }

    if (contentType) {
        payload.headers = {
            'Content-Type': contentType
        }
    }

    // Send PUT request to update the user
    fetch(endpoint + quizId, payload)
    .then(response => response.json())
    .then(data => {
        console.log('Quiz successfully updated', data);
        // Close the modal
        closeModal(modalId);
        location.reload();
    })
    .catch(error => console.error('Error:', error));
}

function deleteEntityYes(endpoint, modalId) {
    let entityId = document.getElementById(modalId).dataset.entityId;
    deleteEntity(endpoint, entityId)
    .then(() => {
        closeModal(modalId);
    });
}

function deleteEntityNo(modalId) {
    closeModal(modalId);
}


function deleteEntity(endpoint, entityId) {
    return fetch(endpoint + entityId, {
        method: 'DELETE'
    })
    .then(data => {
        console.log('User successfully deleted');
        location.reload();
    })
}