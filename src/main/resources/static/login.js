function login(event) {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    const loginRequest = {
        username: username,
        password: password
    };

    fetch('http://localhost:8080/api/authentication/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginRequest)
    }).then(response => {
        if (response.ok) {
            window.location.href = '/admin/index.html';
            alert('Login successful');
        } else {
            alert('Invalid credentials');
        }
    }).catch(error => console.error('Error:', error));
}

function logout(event) {
    event.preventDefault();

    fetch('http://localhost:8080/api/authentication/logout', {
        method: 'POST',
    }).then(response => {
        if (response.ok) {
            window.location.href = '/admin/login.html';
            alert('Logout successful');
        }
    }).catch(error => console.error('Error:', error));
}