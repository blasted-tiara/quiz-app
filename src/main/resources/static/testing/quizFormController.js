function getFormData() {
    let formData = new FormData();
    
    const title = document.getElementById('title-create').value;
    const description = document.getElementById('description-create').value;
    const thumbnailFileInput = document.getElementById('image-picker');

    formData.append('title', title);
    formData.append('description', description);
    if (thumbnailFileInput.files.length > 0) { // make sure a file was selected
        formData.append('thumbnailFile', thumbnailFileInput.files[0]);
    }

    /*
    // Get values from the form
    const title = document.getElementById('title-create').value;
    const description = document.getElementById('description-create').value;

    let result = {
        title,
        description
    };
    */
    
    return formData;
}

function setFormData(quiz) {
    document.getElementById('title-create').value = quiz.title;
    document.getElementById('-create').value = quiz.title;
}