const API_URL = "https://equipmentrental-1.onrender.com";

let loginOtpTimer = null;
let loginOtpSeconds = 60;

// ===============================
// PAGE LOAD
// ===============================
document.addEventListener("DOMContentLoaded", function () {

    const loginForm = document.getElementById("loginForm");
    const verifyOtpBtn = document.getElementById("verifyOtpBtn");
    const resendOtpBtn = document.getElementById("resendOtpBtn");
    const registerForm = document.getElementById("registerForm");

    if (loginForm) {
        loginForm.addEventListener("submit", loginUser);
    }

    if (verifyOtpBtn) {
        verifyOtpBtn.addEventListener("click", verifyLoginOtp);
    }

    if (resendOtpBtn) {
        resendOtpBtn.addEventListener("click", resendLoginOtp);
    }

    if (registerForm) {
        registerForm.addEventListener("submit", registerUser);
    }

    const sendForgotOtpBtn = document.getElementById("sendForgotOtpBtn");
    const verifyForgotOtpBtn = document.getElementById("verifyForgotOtpBtn");
    const resetPasswordBtn = document.getElementById("resetPasswordBtn");

    if (sendForgotOtpBtn) {
        sendForgotOtpBtn.addEventListener("click", sendForgotPasswordOtp);
    }

    if (verifyForgotOtpBtn) {
        verifyForgotOtpBtn.addEventListener("click", verifyForgotOtp);
    }

    if (resetPasswordBtn) {
        resetPasswordBtn.addEventListener("click", resetPassword);
    }

    const forgotPasswordLink = document.getElementById("forgotPasswordLink");

    if (forgotPasswordLink) {
        forgotPasswordLink.addEventListener("click", function (e) {
            e.preventDefault();
            showForgotPassword();
        });
    }

    const backToLoginBtn = document.getElementById("backToLoginBtn");

    if (backToLoginBtn) {
        backToLoginBtn.addEventListener("click", showLogin);
    }
});


// ===============================
// LOGIN
// ===============================
async function loginUser(event) {

    event.preventDefault();

    const email = document.getElementById("loginEmail").value.trim();
    const password = document.getElementById("loginPassword").value;
    const message = document.getElementById("loginMessage");
    const loginBtn = document.getElementById("loginBtn");

    if (!email || !password) {
        showMessage(
            message,
            "Please enter email and password.",
            "error"
        );
        return;
    }

    loginBtn.disabled = true;
    loginBtn.innerText = "Logging in...";

    try {

        const response = await fetch(`${API_URL}/api/users/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });

        const data = await response.json();

        if (!response.ok) {

            showMessage(
                message,
                data.message || "Invalid email or password.",
                "error"
            );

            loginBtn.disabled = false;
            loginBtn.innerText = "Login";

            return;
        }

        /*
         * Backend sends OTP after successful
         * email + password verification.
         */

        if (data.requiresOtp === true) {

            localStorage.setItem("pendingLoginEmail", email);

            showMessage(
                message,
                "OTP sent to your email. Please enter the OTP.",
                "success"
            );

            showOtpSection();

            startOtpTimer();

        } else {

            /*
             * Fallback if backend directly returns user details.
             */

            saveUserSession(data);

            redirectByRole(data.role);
        }

    } catch (error) {

        console.error("Login error:", error);

        showMessage(
            message,
            "Unable to connect to server. Please try again.",
            "error"
        );

    } finally {

        loginBtn.disabled = false;
        loginBtn.innerText = "Login";
    }
}


// ===============================
// SHOW OTP SECTION
// ===============================
function showOtpSection() {

    const otpSection = document.getElementById("otpSection");

    if (otpSection) {
        otpSection.style.display = "block";
    }

    const otp = document.getElementById("otp");

    if (otp) {
        otp.value = "";
        otp.focus();
    }

    const resendOtpBtn = document.getElementById("resendOtpBtn");

    if (resendOtpBtn) {
        resendOtpBtn.disabled = true;
        resendOtpBtn.innerText = "🔄 Resend OTP";
    }

    const otpTimer = document.getElementById("otpTimer");

    if (otpTimer) {
        otpTimer.innerText = "Resend OTP in 60 seconds";
    }
}


// ===============================
// OTP TIMER
// ===============================
function startOtpTimer() {

    clearInterval(loginOtpTimer);

    loginOtpSeconds = 60;

    const resendOtpBtn = document.getElementById("resendOtpBtn");
    const otpTimer = document.getElementById("otpTimer");

    if (resendOtpBtn) {
        resendOtpBtn.disabled = true;
    }

    loginOtpTimer = setInterval(function () {

        loginOtpSeconds--;

        if (otpTimer) {
            otpTimer.innerText =
                `Resend OTP in ${loginOtpSeconds} seconds`;
        }

        if (loginOtpSeconds <= 0) {

            clearInterval(loginOtpTimer);

            if (resendOtpBtn) {
                resendOtpBtn.disabled = false;
                resendOtpBtn.innerText = "🔄 Resend OTP";
            }

            if (otpTimer) {
                otpTimer.innerText = "You can resend the OTP now.";
            }
        }

    }, 1000);
}


// ===============================
// RESEND LOGIN OTP
// ===============================
async function resendLoginOtp() {

    const email =
        localStorage.getItem("pendingLoginEmail") ||
        document.getElementById("loginEmail").value.trim();

    const otpMessage = document.getElementById("otpMessage");
    const resendOtpBtn = document.getElementById("resendOtpBtn");

    if (!email) {

        showMessage(
            otpMessage,
            "Email not found. Please login again.",
            "error"
        );

        return;
    }

    resendOtpBtn.disabled = true;
    resendOtpBtn.innerText = "Sending...";

    try {

        const response = await fetch(
            `${API_URL}/api/users/resend-otp`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email: email
                })
            }
        );

        const data = await response.json();

        if (!response.ok) {

            showMessage(
                otpMessage,
                data.message || "Failed to resend OTP.",
                "error"
            );

            resendOtpBtn.disabled = false;
            resendOtpBtn.innerText = "🔄 Resend OTP";

            return;
        }

        showMessage(
            otpMessage,
            "New OTP sent successfully.",
            "success"
        );

        const otp = document.getElementById("otp");

        if (otp) {
            otp.value = "";
            otp.focus();
        }

        startOtpTimer();

    } catch (error) {

        console.error("Resend OTP error:", error);

        showMessage(
            otpMessage,
            "Unable to connect to server.",
            "error"
        );

        resendOtpBtn.disabled = false;
        resendOtpBtn.innerText = "🔄 Resend OTP";
    }
}


// ===============================
// VERIFY LOGIN OTP
// ===============================
async function verifyLoginOtp() {

    const email =
        localStorage.getItem("pendingLoginEmail") ||
        document.getElementById("loginEmail").value.trim();

    const otp = document.getElementById("otp").value.trim();
    const otpMessage = document.getElementById("otpMessage");
    const verifyBtn = document.getElementById("verifyOtpBtn");

    if (!email) {

        showMessage(
            otpMessage,
            "Email not found. Please login again.",
            "error"
        );

        return;
    }

    if (!otp) {

        showMessage(
            otpMessage,
            "Please enter the OTP.",
            "error"
        );

        return;
    }

    if (otp.length !== 6) {

        showMessage(
            otpMessage,
            "OTP must contain 6 digits.",
            "error"
        );

        return;
    }

    verifyBtn.disabled = true;
    verifyBtn.innerText = "Verifying...";

    try {

        const response = await fetch(
            `${API_URL}/api/users/verify-otp`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email: email,
                    otp: otp
                })
            }
        );

        const data = await response.json();

        if (!response.ok) {

            showMessage(
                otpMessage,
                data.message || "Invalid or expired OTP.",
                "error"
            );

            return;
        }

        clearInterval(loginOtpTimer);

        localStorage.removeItem("pendingLoginEmail");

        saveUserSession(data);

        showMessage(
            otpMessage,
            "Login successful! Redirecting...",
            "success"
        );

        setTimeout(function () {
            redirectByRole(data.role);
        }, 800);

    } catch (error) {

        console.error("OTP verification error:", error);

        showMessage(
            otpMessage,
            "Unable to connect to server.",
            "error"
        );

    } finally {

        verifyBtn.disabled = false;
        verifyBtn.innerText = "Verify OTP";
    }
}


// ===============================
// SAVE USER SESSION
// ===============================
function saveUserSession(data) {

    if (data.id !== undefined) {
        localStorage.setItem("userId", data.id);
    }

    if (data.name) {
        localStorage.setItem("userName", data.name);
    }

    if (data.email) {
        localStorage.setItem("userEmail", data.email);
    }

    if (data.role) {
        localStorage.setItem("userRole", data.role);
    }
}


// ===============================
// REDIRECT BASED ON ROLE
// ===============================
function redirectByRole(role) {

    if (!role) {
        window.location.href = "farmer.html";
        return;
    }

    role = role.toUpperCase();

    if (role === "FARMER") {

        window.location.href = "farmer.html";

    } else if (role === "OWNER") {

        window.location.href = "owner.html";

    } else if (role === "ADMIN") {

        window.location.href = "admin.html";

    } else {

        window.location.href = "farmer.html";
    }
}


// =====================================================
// FORGOT PASSWORD
// =====================================================

function showForgotPassword() {

    const loginSection = document.getElementById("loginSection");

    const forgotSection =
        document.getElementById("forgotPasswordSection");

    if (loginSection) {
        loginSection.style.display = "none";
    }

    if (forgotSection) {
        forgotSection.style.display = "block";
    }

    resetForgotPasswordSteps();
}


// ===============================
// SHOW LOGIN
// ===============================
function showLogin() {

    const loginSection = document.getElementById("loginSection");

    const forgotSection =
        document.getElementById("forgotPasswordSection");

    if (forgotSection) {
        forgotSection.style.display = "none";
    }

    if (loginSection) {
        loginSection.style.display = "block";
    }
}


// ===============================
// FORGOT PASSWORD STEP RESET
// ===============================
function resetForgotPasswordSteps() {

    const emailStep =
        document.getElementById("forgotEmailStep");

    const otpStep =
        document.getElementById("forgotOtpStep");

    const passwordStep =
        document.getElementById("newPasswordStep");

    if (emailStep) {
        emailStep.style.display = "block";
    }

    if (otpStep) {
        otpStep.style.display = "none";
    }

    if (passwordStep) {
        passwordStep.style.display = "none";
    }

    const forgotMessage =
        document.getElementById("forgotMessage");

    if (forgotMessage) {
        forgotMessage.innerText = "";
    }

    localStorage.removeItem("forgotPasswordEmail");
    localStorage.removeItem("forgotPasswordOtp");
}


// ===============================
// SEND FORGOT PASSWORD OTP
// ===============================
async function sendForgotPasswordOtp() {

    const email =
        document.getElementById("forgotEmail").value.trim();

    const message =
        document.getElementById("forgotMessage");

    const button =
        document.getElementById("sendForgotOtpBtn");

    if (!email) {

        showMessage(
            message,
            "Please enter your registered email.",
            "error"
        );

        return;
    }

    button.disabled = true;
    button.innerText = "Sending...";

    try {

        const response = await fetch(
            `${API_URL}/api/users/forgot-password`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email: email
                })
            }
        );

        const data = await response.json();

        if (!response.ok) {

            showMessage(
                message,
                data.message || "Failed to send OTP.",
                "error"
            );

            return;
        }

        localStorage.setItem("forgotPasswordEmail", email);

        showMessage(
            message,
            "Password reset OTP sent to your email.",
            "success"
        );

        const emailStep =
            document.getElementById("forgotEmailStep");

        const otpStep =
            document.getElementById("forgotOtpStep");

        if (emailStep) {
            emailStep.style.display = "none";
        }

        if (otpStep) {
            otpStep.style.display = "block";
        }

        const forgotOtp =
            document.getElementById("forgotOtp");

        if (forgotOtp) {
            forgotOtp.value = "";
            forgotOtp.focus();
        }

    } catch (error) {

        console.error("Forgot password error:", error);

        showMessage(
            message,
            "Unable to connect to server.",
            "error"
        );

    } finally {

        button.disabled = false;
        button.innerText = "Send OTP";
    }
}


// ===============================
// VERIFY FORGOT PASSWORD OTP
// ===============================
async function verifyForgotOtp() {

    const email =
        localStorage.getItem("forgotPasswordEmail") ||
        document.getElementById("forgotEmail").value.trim();

    const otp =
        document.getElementById("forgotOtp").value.trim();

    const message =
        document.getElementById("forgotMessage");

    const button =
        document.getElementById("verifyForgotOtpBtn");

    if (!email) {

        showMessage(
            message,
            "Email not found.",
            "error"
        );

        return;
    }

    if (!otp) {

        showMessage(
            message,
            "Please enter the OTP.",
            "error"
        );

        return;
    }

    if (otp.length !== 6) {

        showMessage(
            message,
            "OTP must contain 6 digits.",
            "error"
        );

        return;
    }

    button.disabled = true;
    button.innerText = "Verifying...";

    try {

        const response = await fetch(
            `${API_URL}/api/users/verify-forgot-otp`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email: email,
                    otp: otp
                })
            }
        );

        const data = await response.json();

        if (!response.ok) {

            showMessage(
                message,
                data.message || "Invalid or expired OTP.",
                "error"
            );

            return;
        }

        localStorage.setItem("forgotPasswordOtp", otp);

        showMessage(
            message,
            "OTP verified. Enter your new password.",
            "success"
        );

        const otpStep =
            document.getElementById("forgotOtpStep");

        const passwordStep =
            document.getElementById("newPasswordStep");

        if (otpStep) {
            otpStep.style.display = "none";
        }

        if (passwordStep) {
            passwordStep.style.display = "block";
        }

    } catch (error) {

        console.error("Forgot OTP verification error:", error);

        showMessage(
            message,
            "Unable to connect to server.",
            "error"
        );

    } finally {

        button.disabled = false;
        button.innerText = "Verify OTP";
    }
}


// ===============================
// RESET PASSWORD
// ===============================
async function resetPassword() {

    const email =
        localStorage.getItem("forgotPasswordEmail");

    const otp =
        localStorage.getItem("forgotPasswordOtp");

    const newPassword =
        document.getElementById("newPassword").value;

    const confirmPassword =
        document.getElementById("confirmNewPassword").value;

    const message =
        document.getElementById("forgotMessage");

    const button =
        document.getElementById("resetPasswordBtn");

    if (!email || !otp) {

        showMessage(
            message,
            "OTP session expired. Please try again.",
            "error"
        );

        return;
    }

    if (!newPassword || !confirmPassword) {

        showMessage(
            message,
            "Please enter both password fields.",
            "error"
        );

        return;
    }

    if (newPassword.length < 6) {

        showMessage(
            message,
            "Password must contain at least 6 characters.",
            "error"
        );

        return;
    }

    if (newPassword !== confirmPassword) {

        showMessage(
            message,
            "Passwords do not match.",
            "error"
        );

        return;
    }

    button.disabled = true;
    button.innerText = "Updating...";

    try {

        const response = await fetch(
            `${API_URL}/api/users/reset-password`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email: email,
                    otp: otp,
                    newPassword: newPassword
                })
            }
        );

        const data = await response.json();

        if (!response.ok) {

            showMessage(
                message,
                data.message || "Failed to reset password.",
                "error"
            );

            return;
        }

        showMessage(
            message,
            "Password reset successful. Please login.",
            "success"
        );

        localStorage.removeItem("forgotPasswordEmail");
        localStorage.removeItem("forgotPasswordOtp");

        document.getElementById("newPassword").value = "";
        document.getElementById("confirmNewPassword").value = "";

        setTimeout(function () {
            showLogin();
        }, 1500);

    } catch (error) {

        console.error("Reset password error:", error);

        showMessage(
            message,
            "Unable to connect to server.",
            "error"
        );

    } finally {

        button.disabled = false;
        button.innerText = "Reset Password";
    }
}


// =====================================================
// REGISTER
// =====================================================

async function registerUser(event) {

    event.preventDefault();

    const name =
        document.getElementById("registerName").value.trim();

    const email =
        document.getElementById("registerEmail").value.trim();

    const password =
        document.getElementById("registerPassword").value;

    const confirmPassword =
        document.getElementById("confirmPassword").value;

    const role =
        document.getElementById("registerRole").value;

    const message =
        document.getElementById("registerMessage");

    if (!name || !email || !password || !confirmPassword || !role) {

        showMessage(
            message,
            "Please fill all fields.",
            "error"
        );

        return;
    }

    if (password.length < 6) {

        showMessage(
            message,
            "Password must contain at least 6 characters.",
            "error"
        );

        return;
    }

    if (password !== confirmPassword) {

        showMessage(
            message,
            "Passwords do not match.",
            "error"
        );

        return;
    }

    try {

        const response = await fetch(
            `${API_URL}/api/users`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    name: name,
                    email: email,
                    password: password,
                    role: role
                })
            }
        );

        const data = await response.json();

        if (!response.ok) {

            showMessage(
                message,
                data.message || "Registration failed.",
                "error"
            );

            return;
        }

        showMessage(
            message,
            "Registration successful! Please login.",
            "success"
        );

        document.getElementById("registerForm").reset();

        setTimeout(function () {

            const registerSection =
                document.getElementById("registerSection");

            const loginSection =
                document.getElementById("loginSection");

            if (registerSection) {
                registerSection.style.display = "none";
            }

            if (loginSection) {
                loginSection.style.display = "block";
            }

        }, 1200);

    } catch (error) {

        console.error("Registration error:", error);

        showMessage(
            message,
            "Unable to connect to server.",
            "error"
        );
    }
}


// =====================================================
// LOGOUT
// =====================================================

function logout() {

    clearInterval(loginOtpTimer);

    localStorage.removeItem("userId");
    localStorage.removeItem("userName");
    localStorage.removeItem("userEmail");
    localStorage.removeItem("userRole");

    localStorage.removeItem("pendingLoginEmail");

    localStorage.removeItem("forgotPasswordEmail");
    localStorage.removeItem("forgotPasswordOtp");

    window.location.href = "login.html";
}


// =====================================================
// MESSAGE FUNCTION
// =====================================================

function showMessage(element, text, type) {

    if (!element) {
        return;
    }

    element.innerText = text;

    element.className = "";

    if (type === "success") {

        element.classList.add("success-message");

    } else if (type === "error") {

        element.classList.add("error-message");

    } else {

        element.classList.add("info-message");
    }
}


// =====================================================
// REGISTER SECTION TOGGLE
// =====================================================

function showRegister() {

    const loginSection =
        document.getElementById("loginSection");

    const registerSection =
        document.getElementById("registerSection");

    const forgotSection =
        document.getElementById("forgotPasswordSection");

    if (loginSection) {
        loginSection.style.display = "none";
    }

    if (forgotSection) {
        forgotSection.style.display = "none";
    }

    if (registerSection) {
        registerSection.style.display = "block";
    }
}


// =====================================================
// BACK TO LOGIN FROM REGISTER
// =====================================================

function backToLogin() {

    const registerSection =
        document.getElementById("registerSection");

    const loginSection =
        document.getElementById("loginSection");

    if (registerSection) {
        registerSection.style.display = "none";
    }

    if (loginSection) {
        loginSection.style.display = "block";
    }
}