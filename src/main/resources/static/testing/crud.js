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

function updateUser(e) {
    e.preventDefault();
    
    // Get the new values from the form
    let newUsername = document.getElementById('edit-username-input').value;
    let newPassword = document.getElementById('edit-password-input').value;

    const inputs = document.getElementById('edit-role-selector').getElementsByTagName('input');
    const newRoles = [...inputs].filter(e => e.checked).map(e => e.dataset.enumValue);
    
    const userId = document.getElementById('edit-user-form').dataset.userId;

    let formData = {
        username: newUsername,
        roles: newRoles
    }
    
    if (newPassword !== "") {
        formData.password = newPassword; 
    }

    // Send PUT request to update the user
    fetch('http://localhost:8080/api/users/' + userId, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData) // Send the FormData object
    })
    .then(response => response.json())
    .then(data => {
        console.log('Video successfully updated', data);
        // Close the modal
        closeModal('edit-user-modal');
        location.reload();
    })
    .catch(error => console.error('Error:', error));
}

function deleteUserYes() {
    let userId = document.getElementById('delete-user-modal').dataset.userId;
    deleteUser(userId)
    .then(() => {
        closeModal('delete-user-modal');
    });
}

function deleteUserNo() {
    closeModal('delete-user-modal');
}


function deleteUser(userId) {
    return fetch( + userId, {
        method: 'DELETE'
    })
    .then(data => {
        console.log('User successfully deleted');
        location.reload();
    })
}