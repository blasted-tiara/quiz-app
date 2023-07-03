function createVideoTableRow(video, index) {
    let row = document.createElement('tr');

    let thumbnailCell = document.createElement('td');
    let img = document.createElement('img');
    img.src = constructVideoThumbnailURL(video);
    img.alt = "Video thumbnail";
    thumbnailCell.appendChild(img);

    let titleCell = document.createElement('td');
    titleCell.textContent = video.title;

    let votesCell = document.createElement('td');
    votesCell.textContent = `${video.positiveVotes}/${video.totalVotes}`;

    let rankCell = document.createElement('td');
    rankCell.textContent = index + 1;

    row.appendChild(thumbnailCell);
    row.appendChild(titleCell);
    row.appendChild(votesCell);
    row.appendChild(rankCell);

    return row;
}

function createAdminVideoTableRow(video, index) {
    let row = document.createElement('tr');

    let thumbnailCell = document.createElement('td');
    let img = document.createElement('img');
    img.src = constructVideoThumbnailURL(video);
    img.alt = "Video thumbnail";
    thumbnailCell.appendChild(img);

    let titleCell = document.createElement('td');
    titleCell.textContent = video.title;

    let votesCell = document.createElement('td');
    votesCell.textContent = `${video.positiveVotes}/${video.totalVotes}`;

    let rankCell = document.createElement('td');
    rankCell.textContent = index + 1;

    let actionsCell = document.createElement("td");

    let editButton = document.createElement("button");
    editButton.className = "action-button";

    let editIcon = document.createElement("i");
    editIcon.className = "fas fa-edit";
    
    editButton.appendChild(editIcon).addEventListener('click', function (e) {
        e.preventDefault();

        // Fetch the current data for the video
        fetch('http://localhost:8080/api/videos/' + video.id)
            .then(response => response.json())
            .then(video => {
                const mdcTextFieldElement = document.getElementById('edit-title-component'); // Change this selector to your needs
                const nativeInputField = mdcTextFieldElement.querySelector('input');
                nativeInputField.value = video.title;
                
                const mdcTextField = mdc.textField.MDCTextField.attachTo(mdcTextFieldElement);
                mdcTextField.foundation.setValue(video.title);

                document.getElementById('image-preview').src = constructVideoThumbnailURL(video);
                document.getElementById('edit-video-form').dataset.videoId = video.id;
                
                // Show the modal
                showModal('edit-video-modal');
            });
    });

    let deleteButton = document.createElement("button");
    deleteButton.className = "action-button";

    let deleteIcon = document.createElement("i");
    deleteIcon.className = "fas fa-trash-alt";

    deleteButton.appendChild(deleteIcon);
    deleteButton.addEventListener('click', function (e) {
        e.preventDefault();
        
        document.getElementById('delete-video-modal').dataset.videoId = video.id;

        showModal('delete-video-modal');
    })

    actionsCell.appendChild(editButton);
    actionsCell.appendChild(deleteButton);

    row.appendChild(thumbnailCell);
    row.appendChild(titleCell);
    row.appendChild(votesCell);
    row.appendChild(rankCell);
    row.appendChild(actionsCell);

    return row;
}

function constructVideoThumbnailURL(video) {
    return video.thumbnailURL ? `http://localhost:8080/api/images/${video.thumbnailURL}` : `https://img.youtube.com/vi/${video.youtubeId}/3.jpg`;
}

function emptyTable(tableId) {
    let table = document.getElementById(tableId);
    let rows = table.getElementsByTagName('tr');

    for (let i = rows.length - 1; i > 0; i--) {
        table.deleteRow(i);
    }
}