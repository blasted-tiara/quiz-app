function createUsersTableRow(user) {
    let row = document.createElement('tr');

    let usernameCell = document.createElement('td');
    usernameCell.textContent = user.username;

    let rolesCell = document.createElement('td');
    rolesCell.textContent = user.roles;

    let actionsCell = document.createElement("td");

    let editButton = document.createElement("button");
    editButton.className = "action-button";

    let editIcon = document.createElement("i");
    editIcon.className = "fas fa-edit";
    
    editButton.appendChild(editIcon).addEventListener('click', function (e) {
        e.preventDefault();

        // Fetch the current data for the user
        fetch(userEndpoint + user.id)
            .then(response => response.json())
            .then(user => {
                const mdcTextFieldElement = document.getElementById('edit-username-component'); // Change this selector to your needs
                const nativeInputField = mdcTextFieldElement.querySelector('input');
                nativeInputField.value = user.username;
                
                const mdcTextField = mdc.textField.MDCTextField.attachTo(mdcTextFieldElement);
                mdcTextField.foundation.setValue(user.username);
                
                populateRoles(user.roles);
                document.getElementById('edit-user-form').dataset.entityId = user.id;
                
                // Show the modal
                showModal('edit-user-modal');
            });
    });

    let deleteButton = document.createElement("button");
    deleteButton.className = "action-button";

    let deleteIcon = document.createElement("i");
    deleteIcon.className = "fas fa-trash-alt";

    deleteButton.appendChild(deleteIcon);
    deleteButton.addEventListener('click', function (e) {
        e.preventDefault();
        
        document.getElementById('delete-user-modal').dataset.entityId = user.id;

        showModal('delete-user-modal');
    })

    actionsCell.appendChild(editButton);
    actionsCell.appendChild(deleteButton);

    row.appendChild(usernameCell);
    row.appendChild(rolesCell);
    row.appendChild(actionsCell);

    return row;
}

function populateRoles(roles) {
    const rolesContainer = document.getElementById('edit-role-selector');
    rolesContainer.innerHTML = "";

    // Enum to human-readable name mapping
    const roleNames = {
        'ADMIN': 'Admin',
        'MODERATOR': 'Moderator'
    };
    
    for (let role in roleNames) {
        // Create elements
        let formField = document.createElement('div');
        formField.classList.add('mdc-form-field');

        let checkbox = document.createElement('div');
        checkbox.classList.add('mdc-checkbox');

        let input = document.createElement('input');
        input.type = 'checkbox';
        input.classList.add('mdc-checkbox__native-control');
        input.id = `${role.toLowerCase()}-role`;
        input.dataset.enumValue = role;
        input.checked = roles.includes(role);

        let background = document.createElement('div');
        background.classList.add('mdc-checkbox__background');

        let svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
        svg.classList.add('mdc-checkbox__checkmark');
        svg.setAttribute('viewBox', '0 0 24 24');

        let path = document.createElementNS("http://www.w3.org/2000/svg", "path");
        path.classList.add('mdc-checkbox__checkmark-path');
        path.setAttribute('fill', 'none');
        path.setAttribute('d', 'M1.73,12.91 8.1,19.28 22.79,4.59');

        let mixedmark = document.createElement('div');
        mixedmark.classList.add('mdc-checkbox__mixedmark');

        let ripple = document.createElement('div');
        ripple.classList.add('mdc-checkbox__ripple');

        let label = document.createElement('label');
        label.htmlFor = `${role.toLowerCase()}-role`;
        label.innerText = roleNames[role];

        // Assemble elements
        svg.appendChild(path);
        background.appendChild(svg);
        background.appendChild(mixedmark);
        checkbox.appendChild(input);
        checkbox.appendChild(background);
        checkbox.appendChild(ripple);
        formField.appendChild(checkbox);
        formField.appendChild(label);

        // Append to the container
        rolesContainer.appendChild(formField);
    };

    // After adding elements, the Material components need to be initialized
    const checkboxes = rolesContainer.querySelectorAll('.mdc-checkbox');
    for (let i = 0; i < checkboxes.length; i++) {
        new mdc.checkbox.MDCCheckbox(checkboxes[i]);
    }
}