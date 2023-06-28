let isVisible = "is-visible";

function showModal(modalId) {
    document.getElementById(modalId).classList.add(isVisible);
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.remove(isVisible);
}

function addShowModalListener(elementId, modalId) {
    document.getElementById(elementId).addEventListener("click", function () {
        showModal(modalId);
    });
}

function addCloseModalListeners() {
    const closeEls = document.querySelectorAll(".close-modal");

    // Close on X press
    for (const el of closeEls) {
        el.addEventListener("click", function () {
            this.parentElement.parentElement.parentElement.classList.remove(isVisible);
        });
    }

    // Close when clicked outside of the document
    document.addEventListener("click", e => {
        if (e.target == document.querySelector(".modal.is-visible")) {
            document.querySelector(".modal.is-visible").classList.remove(isVisible);
        }
    });

    // Close on ESC press
    document.addEventListener("keyup", e => {
        if (e.key == "Escape" && document.querySelector(".modal.is-visible")) {
            document.querySelector(".modal.is-visible").classList.remove(isVisible);
        }
    });
}