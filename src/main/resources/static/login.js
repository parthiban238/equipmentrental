const API_URL = "http://localhost:8081";

document.getElementById("loginForm").addEventListener("submit", async function(event) {

    event.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const loginMessage = document.getElementById("loginMessage");

    // Empty field check
    if (email === "" || password === "") {
        loginMessage.innerText = "Please enter email and password.";
        return;
    }

    try {

        // Login API
        const response = await fetch(
            `${API_URL}/api/users/login`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email: email,
                    password: password
                })
            }
        );

        // Check login response
        if (!response.ok) {
            loginMessage.innerText =
                "Invalid email or password.";
            return;
        }

        // Get user data
        const user = await response.json();

        console.log("Login successful:", user);

        // Save user details
        localStorage.setItem("userId", user.id);
        localStorage.setItem("userName", user.name);
        localStorage.setItem("userRole", user.role);

        loginMessage.innerText =
            "Login successful!";

        // Get role safely
        const role =
            String(user.role).trim().toUpperCase();

        console.log("User Role:", role);

        // Redirect
        setTimeout(function() {

            if (role === "FARMER") {

                window.location.href =
                    "http://localhost:8081/farmer.html";

            } else if (role === "OWNER") {

                window.location.href =
                    "http://localhost:8081/owner.html";

            } else if (role === "ADMIN") {

                window.location.href =
                    "http://localhost:8081/admin.html";

            } else {

                loginMessage.innerText =
                    "Unknown user role: " + user.role;
            }

        }, 500);

    } catch (error) {

        console.error("Login Error:", error);

        loginMessage.innerText =
            "Backend connection failed. Please check Spring Boot.";
    }

});