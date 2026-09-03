
const API_URL = "http://localhost:8081";

document.getElementById("registerForm").addEventListener("submit", async function (event) {

    event.preventDefault();

    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;
    const role = document.getElementById("role").value;

    const message = document.getElementById("message");

    // Basic validation
    if (!name || !email || !password || !role) {
        message.textContent = "Please fill all fields.";
        message.style.color = "red";
        return;
    }

    const userData = {
        name: name,
        email: email,
        password: password,
        role: role
    };

    try {

        const response = await fetch(`${API_URL}/api/users`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userData)
        });

        if (!response.ok) {

            const errorText = await response.text();

            console.error("Registration error:", errorText);

            message.textContent = "Registration failed.";
            message.style.color = "red";

            return;
        }

        const registeredUser = await response.json();

        console.log("Registered User:", registeredUser);

        message.textContent = "Registration successful!";
        message.style.color = "green";

        // Clear form
        document.getElementById("registerForm").reset();

        // Go to login page after 1.5 seconds
        setTimeout(() => {
            window.location.href = "login.html";
        }, 1500);

    } catch (error) {

        console.error("Error:", error);

        message.textContent =
            "Unable to connect to server. Please try again.";

        message.style.color = "red";
    }
});

