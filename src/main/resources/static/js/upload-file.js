// Wait for DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    // Cache DOM elements
    const elements = {
        uploadButton: document.getElementById("uploadButton"),
        backgroundImage: document.getElementById("backgroundImage"),
        photoPreview: document.getElementById("photoPreview"),
        bookTitle: document.getElementById("bookTitle"), // Note: typo in "title"
        authorName: document.getElementById("authorName"),
        form: document.querySelector("form") // Assuming you have a form
    };

    // Event listeners
    if (elements.uploadButton && elements.backgroundImage) {
        elements.uploadButton.addEventListener("click", function() {
            console.log("Upload button clicked!");
            elements.backgroundImage.click();
        });

        elements.backgroundImage.addEventListener("change", handleFileSelect);
    }

    // Form validation
    if (elements.form) {
        elements.form.addEventListener("submit", function(event) {
            if (!validateForm()) {
                event.preventDefault(); // Prevent form submission if validation fails
            }
        });
    }

    // File selection handler
    function handleFileSelect(event) {
        console.log("File selected!");
        const file = event.target.files[0];
        
        if (file) {
            // Validate file type if needed
            if (!file.type.match('image.*')) {
                alert("Please select an image file");
                return;
            }

            const reader = new FileReader();

            reader.onload = function(e) {
                console.log("File read successfully!");
                if (elements.photoPreview) {
                    elements.photoPreview.src = e.target.result;

                    elements.photoPreview.onload = function() {
                        elements.photoPreview.style.height = "auto";
                        elements.photoPreview.style.maxHeight = "400px";
                    };
                }
            };

            reader.readAsDataURL(file);
        }
    }

    // Form validation function
    function validateForm() {
        let isValid = true;

        // Validate book title
        if (elements.bookTitle && elements.bookTitle.value.trim() === "") {
            alert("Please enter a book title");
            isValid = false;
        }

        // Validate author name
        if (elements.authorName && elements.authorName.value.trim() === "") {
            alert("Please enter an author name");
            isValid = false;
        }

        // Add more validations as needed

        return isValid;
    }
});