const userEndpoint = 'http://localhost:8080/api/user/';

function getUserCreateFormValues() {
    // Get values from the form
    const username = document.getElementById('new-username-input').value;
    const password = document.getElementById('new-password-input').value;

    const inputs = document.getElementById('role-selector').getElementsByTagName('input');
    const roles = [...inputs].filter(e => e.checked).map(e => e.dataset.enumValue);


    // Create user object
    let userData = {
        username,
        password,
        roles
    };

    return JSON.stringify(userData);
}

function getUserEditFormValues() {
    // Get values from the form
    const username = document.getElementById('edit-username-input').value;
    const password = document.getElementById('edit-password-input').value;

    const inputs = document.getElementById('edit-role-selector').getElementsByTagName('input');
    const roles = [...inputs].filter(e => e.checked).map(e => e.dataset.enumValue);

    // Create user object
    let userData = {
        username,
        password,
        roles
    };

    return JSON.stringify(userData);
}