function readURL(input, imageId) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            $(imageId).attr('src', e.target.result);
        }

        reader.readAsDataURL(input.files[0]);
    }
}