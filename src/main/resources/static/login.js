const API_URL = "http://localhost:8081/api/users";

let loggedInEmail = "";
let forgotPasswordEmail = "";
let forgotPasswordOtp = "";


// ==========================================
// SHOW / HIDE PASSWORD
// ==========================================

function togglePassword() {

    const password =
        document.getElementById("loginPassword");

    if (password.type === "password") {

        password.type = "text";

    } else {

        password.type = "password";

    }
}


// ==========================================
// LOGIN
// ==========================================

document
    .getElementById("loginForm")
    .addEventListener("submit", async function(event) {

        event.preventDefault();

        const email =
            document
                .getElementById("loginEmail")
                .value
                .trim();

        const password =
            document
                .getElementById("loginPassword")
                .value;

        const message =
            document.getElementById("loginMessage");

        const loginBtn =
            document.getElementById("loginBtn");


        if (!email || !password) {

            message.innerText =
                "Please enter email and password.";

            message.style.color = "red";

            return;
        }


        message.innerText =
            "Checking your credentials...";

        message.style.color = "#555";


        loginBtn.disabled = true;

        loginBtn.innerText =
            "Sending OTP...";


        try {

            const response = await fetch(
                API_URL + "/login",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        email: email,
                        password: password
                    })
                }
            );


            const data =
                await response.json();


            if (!response.ok) {

                throw new Error(
                    data.message ||
                    data.error ||
                    "Invalid email or password"
                );
            }


            loggedInEmail =
                data.email || email;


            message.innerText =
                "✓ OTP sent successfully to your email.";

            message.style.color =
                "#218838";


            document
                .getElementById("otpSection")
                .style.display = "block";


            document
                .getElementById("otpSection")
                .scrollIntoView({
                    behavior: "smooth",
                    block: "center"
                });


            loginBtn.innerText =
                "OTP Sent ✓";


        } catch (error) {

            console.error(error);

            message.innerText =
                error.message;

            message.style.color =
                "red";


            loginBtn.disabled =
                false;

            loginBtn.innerText =
                "Login & Get OTP";
        }

    });


// ==========================================
// VERIFY LOGIN OTP
// ==========================================

document
    .getElementById("verifyOtpBtn")
    .addEventListener("click", async function() {

        const otp =
            document
                .getElementById("otp")
                .value
                .trim();

        const otpMessage =
            document.getElementById("otpMessage");

        const verifyBtn =
            document.getElementById("verifyOtpBtn");


        if (!/^\d{6}$/.test(otp)) {

            otpMessage.innerText =
                "Please enter a valid 6-digit OTP.";

            otpMessage.style.color =
                "red";

            return;
        }


        verifyBtn.disabled =
            true;

        verifyBtn.innerText =
            "Verifying...";


        otpMessage.innerText =
            "Verifying OTP...";

        otpMessage.style.color =
            "#555";


        try {

            const response = await fetch(
                API_URL + "/verify-otp",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        email: loggedInEmail,
                        otp: otp
                    })
                }
            );


            const user =
                await response.json();


            if (!response.ok) {

                throw new Error(
                    user.message ||
                    user.error ||
                    "Invalid OTP"
                );
            }


            // ======================================
            // SAVE USER DETAILS
            // ======================================

            localStorage.setItem(
                "userId",
                user.id
            );

            localStorage.setItem(
                "userName",
                user.name
            );

            localStorage.setItem(
                "userRole",
                user.role
            );

            localStorage.setItem(
                "userEmail",
                user.email
            );


            // ======================================
            // REMEMBER ME
            // ======================================

            const rememberMe =
                document.getElementById("rememberMe");


            if (rememberMe && rememberMe.checked) {

                localStorage.setItem(
                    "rememberMe",
                    "true"
                );

            } else {

                localStorage.removeItem(
                    "rememberMe"
                );

            }


            otpMessage.innerText =
                "✓ OTP verified! Login successful.";

            otpMessage.style.color =
                "#218838";


            // ======================================
            // REDIRECT
            // ======================================

            setTimeout(() => {

                if (user.role === "FARMER") {

                    window.location.href =
                        "farmer.html";

                }

                else if (user.role === "OWNER") {

                    window.location.href =
                        "owner.html";

                }

                else if (user.role === "ADMIN") {

                    window.location.href =
                        "admin.html";

                }

                else {

                    window.location.href =
                        "index.html";

                }

            }, 700);


        } catch (error) {

            console.error(error);

            otpMessage.innerText =
                error.message;

            otpMessage.style.color =
                "red";


            verifyBtn.disabled =
                false;

            verifyBtn.innerText =
                "✓ Verify OTP & Login";
        }

    });


// ==========================================
// SHOW FORGOT PASSWORD
// ==========================================

function showForgotPassword() {

    document
        .getElementById("loginForm")
        .style.display = "none";


    document
        .getElementById("otpSection")
        .style.display = "none";


    document
        .getElementById("forgotPasswordSection")
        .style.display = "block";


    document
        .getElementById("forgotEmailStep")
        .style.display = "block";


    document
        .getElementById("forgotOtpStep")
        .style.display = "none";


    document
        .getElementById("newPasswordStep")
        .style.display = "none";


    document
        .getElementById("forgotMessage")
        .innerText = "";


    const loginEmail =
        document
            .getElementById("loginEmail")
            .value
            .trim();


    document
        .getElementById("forgotEmail")
        .value = loginEmail;


    document
        .getElementById("forgotPasswordSection")
        .scrollIntoView({
            behavior: "smooth",
            block: "center"
        });
}


// ==========================================
// HIDE FORGOT PASSWORD
// ==========================================

function hideForgotPassword() {

    document
        .getElementById("forgotPasswordSection")
        .style.display = "none";


    document
        .getElementById("loginForm")
        .style.display = "block";


    document
        .getElementById("forgotMessage")
        .innerText = "";

}


// ==========================================
// SEND FORGOT PASSWORD OTP
// ==========================================

document
    .getElementById("sendForgotOtpBtn")
    .addEventListener("click", async function() {


        const email =
            document
                .getElementById("forgotEmail")
                .value
                .trim();


        const message =
            document
                .getElementById("forgotMessage");


        const button =
            document
                .getElementById("sendForgotOtpBtn");


        if (!email) {

            message.innerText =
                "Please enter your registered email.";

            message.style.color =
                "red";

            return;
        }


        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {

            message.innerText =
                "Please enter a valid email address.";

            message.style.color =
                "red";

            return;
        }


        forgotPasswordEmail =
            email;


        button.disabled =
            true;

        button.innerText =
            "Sending OTP...";


        message.innerText =
            "Sending password reset OTP...";

        message.style.color =
            "#555";


        try {

            const response = await fetch(
                API_URL + "/forgot-password",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        email: email
                    })
                }
            );


            const data =
                await response.json();


            if (!response.ok) {

                throw new Error(
                    data.message ||
                    data.error ||
                    "Unable to send OTP"
                );
            }


            message.innerText =
                "✓ Password reset OTP sent to your email.";

            message.style.color =
                "#218838";


            document
                .getElementById("forgotEmailStep")
                .style.display = "none";


            document
                .getElementById("forgotOtpStep")
                .style.display = "block";


            document
                .getElementById("forgotOtp")
                .focus();


        } catch (error) {

            console.error(error);

            message.innerText =
                error.message;

            message.style.color =
                "red";


            button.disabled =
                false;

            button.innerText =
                "📩 Send Reset OTP";
        }

    });


// ==========================================
// VERIFY FORGOT PASSWORD OTP
// ==========================================

document
    .getElementById("verifyForgotOtpBtn")
    .addEventListener("click", async function() {


        const email =
            document
                .getElementById("forgotEmail")
                .value
                .trim();


        const otp =
            document
                .getElementById("forgotOtp")
                .value
                .trim();


        const message =
            document
                .getElementById("forgotMessage");


        const button =
            document
                .getElementById("verifyForgotOtpBtn");


        if (!/^\d{6}$/.test(otp)) {

            message.innerText =
                "Please enter a valid 6-digit OTP.";

            message.style.color =
                "red";

            return;
        }


        button.disabled =
            true;

        button.innerText =
            "Verifying...";


        message.innerText =
            "Verifying OTP...";

        message.style.color =
            "#555";


        try {

            const response = await fetch(
                API_URL + "/verify-forgot-otp",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        email: email,
                        otp: otp
                    })
                }
            );


            const data =
                await response.json();


            if (!response.ok) {

                throw new Error(
                    data.message ||
                    data.error ||
                    "Invalid OTP"
                );
            }


            // Save OTP for reset-password API
            forgotPasswordOtp =
                otp;


            message.innerText =
                "✓ OTP verified. Create your new password.";

            message.style.color =
                "#218838";


            document
                .getElementById("forgotOtpStep")
                .style.display = "none";


            document
                .getElementById("newPasswordStep")
                .style.display = "block";


            document
                .getElementById("newPassword")
                .focus();


        } catch (error) {

            console.error(error);

            message.innerText =
                error.message;

            message.style.color =
                "red";


            button.disabled =
                false;

            button.innerText =
                "✓ Verify OTP";
        }

    });


// ==========================================
// RESET PASSWORD
// ==========================================

document
    .getElementById("resetPasswordBtn")
    .addEventListener("click", async function() {


        const email =
            document
                .getElementById("forgotEmail")
                .value
                .trim();


        const otp =
            forgotPasswordOtp ||
            document
                .getElementById("forgotOtp")
                .value
                .trim();


        const newPassword =
            document
                .getElementById("newPassword")
                .value;


        const confirmPassword =
            document
                .getElementById("confirmNewPassword")
                .value;


        const message =
            document
                .getElementById("forgotMessage");


        const button =
            document
                .getElementById("resetPasswordBtn");


        if (!newPassword || !confirmPassword) {

            message.innerText =
                "Please enter both password fields.";

            message.style.color =
                "red";

            return;
        }


        if (newPassword.length < 4) {

            message.innerText =
                "Password must contain at least 4 characters.";

            message.style.color =
                "red";

            return;
        }


        if (newPassword !== confirmPassword) {

            message.innerText =
                "Passwords do not match.";

            message.style.color =
                "red";

            return;
        }


        if (!otp) {

            message.innerText =
                "OTP verification is required.";

            message.style.color =
                "red";

            return;
        }


        button.disabled =
            true;

        button.innerText =
            "Resetting Password...";


        message.innerText =
            "Updating your password...";

        message.style.color =
            "#555";


        try {

            const response = await fetch(
                API_URL + "/reset-password",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        email: email,

                        otp: otp,

                        newPassword: newPassword

                    })
                }
            );


            const data =
                await response.json();


            if (!response.ok) {

                throw new Error(
                    data.message ||
                    data.error ||
                    "Password reset failed"
                );
            }


            message.innerText =
                "✓ Password reset successful! You can now login.";

            message.style.color =
                "#218838";


            document
                .getElementById("newPassword")
                .value = "";


            document
                .getElementById("confirmNewPassword")
                .value = "";


            forgotPasswordOtp = "";


            setTimeout(function() {

                hideForgotPassword();


                document
                    .getElementById("loginEmail")
                    .value = email;


                document
                    .getElementById("loginPassword")
                    .value = "";


                const loginBtn =
                    document.getElementById("loginBtn");


                loginBtn.disabled =
                    false;


                loginBtn.innerText =
                    "Login & Get OTP";


            }, 1500);


        } catch (error) {

            console.error(error);

            message.innerText =
                error.message;

            message.style.color =
                "red";


            button.disabled =
                false;

            button.innerText =
                "🔒 Reset Password";
        }

    });


// ==========================================
// REGISTER
// ==========================================

document
    .getElementById("registerForm")
    .addEventListener("submit", async function(event) {

        event.preventDefault();


        const name =
            document
                .getElementById("registerName")
                .value
                .trim();


        const email =
            document
                .getElementById("registerEmail")
                .value
                .trim();


        const password =
            document
                .getElementById("registerPassword")
                .value;


        const confirmPassword =
            document
                .getElementById("confirmPassword")
                .value;


        const role =
            document
                .getElementById("registerRole")
                .value;


        const message =
            document
                .getElementById("registerMessage");


        if (!name || !email || !password || !confirmPassword || !role) {

            message.innerText =
                "Please fill all fields.";

            message.style.color =
                "red";

            return;
        }


        if (password !== confirmPassword) {

            message.innerText =
                "Passwords do not match.";

            message.style.color =
                "red";

            return;
        }


        message.innerText =
            "Creating your account...";

        message.style.color =
            "#555";


        try {

            const response = await fetch(
                API_URL,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        name: name,

                        email: email,

                        password: password,

                        role: role

                    })
                }
            );


            if (!response.ok) {

                const errorText =
                    await response.text();


                throw new Error(
                    errorText ||
                    "Registration failed"
                );
            }


            const user =
                await response.json();


            message.innerText =
                "✓ Registration successful! Welcome email sent.";

            message.style.color =
                "#218838";


            document
                .getElementById("registerForm")
                .reset();


            document
                .getElementById("loginEmail")
                .value =
                user.email;


            setTimeout(function() {

                scrollToLogin();

            }, 1000);


        } catch (error) {

            console.error(error);

            message.innerText =
                error.message;

            message.style.color =
                "red";
        }

    });


// ==========================================
// SCROLL TO REGISTER
// ==========================================

function scrollToRegister() {

    document
        .getElementById("registerSection")
        .scrollIntoView({
            behavior: "smooth"
        });

}


// ==========================================
// SCROLL TO LOGIN
// ==========================================

function scrollToLogin() {

    window.scrollTo({

        top: 0,

        behavior: "smooth"

    });

}