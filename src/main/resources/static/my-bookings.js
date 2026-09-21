const API_URL = "https://equipmentrental-1.onrender.com";

const userId = localStorage.getItem("userId");
const userRole = localStorage.getItem("userRole");

console.log("MY BOOKINGS JS LOADED");
console.log("User ID:", userId);
console.log("User Role:", userRole);

if (!userId) {
    alert("Please login first.");
    window.location.href = "login.html";
}

if (userRole && userRole.toUpperCase() !== "FARMER") {
    alert("Farmer access required.");
    window.location.href = "login.html";
}


// ===============================
// LOAD MY BOOKINGS
// ===============================
async function loadBookings() {

    const bookingList = document.getElementById("bookingList");

    if (!bookingList) {
        console.error("bookingList element not found");
        return;
    }

    bookingList.innerHTML = `
        <div class="loading">
            Loading your bookings...
        </div>
    `;

    try {

        console.log("Fetching all rentals...");

        const response = await fetch(
            `${API_URL}/api/rentals`
        );

        console.log("Rental API Status:", response.status);

        if (!response.ok) {
            throw new Error(
                "Unable to fetch bookings. Status: " + response.status
            );
        }

        const allBookings = await response.json();

        console.log("All Bookings:", allBookings);

        // Filter bookings for logged-in farmer
        const bookings = allBookings.filter(
            booking =>
                String(booking.userId) === String(userId)
        );

        console.log("My Bookings:", bookings);

        if (!bookings || bookings.length === 0) {

            bookingList.innerHTML = `
                <div class="empty">
                    <h3>📋 No Bookings Found</h3>
                    <p>
                        You have not booked any agricultural equipment yet.
                    </p>

                    <button
                        class="refresh-btn"
                        onclick="goToEquipment()">
                        🚜 Browse Equipment
                    </button>
                </div>
            `;

            return;
        }

        bookingList.innerHTML = "";

        // Display each booking
        for (const booking of bookings) {

            const card = document.createElement("div");

            card.className = "booking-card";

            const status =
                String(
                    booking.status || "PENDING"
                ).toUpperCase();

            let statusClass = "pending";

            if (status === "APPROVED") {
                statusClass = "approved";
            }
            else if (status === "REJECTED") {
                statusClass = "rejected";
            }
            else if (status === "COMPLETED") {
                statusClass = "completed";
            }

            const amount =
                booking.totalAmount != null
                    ? "₹ " +
                      Number(
                          booking.totalAmount
                      ).toFixed(2)
                    : "₹ 0.00";


            // Rating button only for completed bookings
            let ratingSection = "";

            if (status === "COMPLETED") {

                ratingSection = `
                    <button
                        class="rating-btn"
                        onclick="
                            openRating(
                                ${booking.id},
                                ${booking.equipmentId}
                            )
                        ">
                        ⭐ Give Rating & Review
                    </button>
                `;
            }


            // Complaint button
            const complaintButton = `
                <button
                    class="complaint-btn"
                    onclick="
                        openComplaintForm(
                            ${booking.id},
                            ${booking.equipmentId}
                        )
                    ">
                    📢 Report Issue
                </button>
            `;


            card.innerHTML = `

                <div class="booking-header">

                    <h3>
                        🚜 Booking #${booking.id}
                    </h3>

                    <span class="status ${statusClass}">
                        ${status}
                    </span>

                </div>


                <div class="details">

                    <div class="detail-box">
                        <strong>👨‍🌾 Farmer ID</strong>
                        <span>
                            ${booking.userId}
                        </span>
                    </div>


                    <div class="detail-box">
                        <strong>🚜 Equipment ID</strong>
                        <span>
                            ${booking.equipmentId}
                        </span>
                    </div>


                    <div class="detail-box">
                        <strong>📅 Start Date</strong>
                        <span>
                            ${booking.startDate}
                        </span>
                    </div>


                    <div class="detail-box">
                        <strong>📅 End Date</strong>
                        <span>
                            ${booking.endDate}
                        </span>
                    </div>


                    <div class="detail-box">
                        <strong>💰 Total Amount</strong>
                        <span>
                            ${amount}
                        </span>
                    </div>


                    <div class="detail-box">
                        <strong>📌 Booking Status</strong>
                        <span>
                            ${status}
                        </span>
                    </div>

                </div>


                <div class="booking-actions">

                    ${ratingSection}

                    ${complaintButton}

                </div>


                <!-- COMPLAINT FORM -->

                <div
                    id="complaint-form-${booking.id}"
                    class="complaint-form">

                    <h3>
                        📢 Report an Issue
                    </h3>

                    <p>
                        Booking #${booking.id}
                    </p>


                    <label>
                        Complaint Type
                    </label>

                    <select
                        id="complaint-type-${booking.id}">

                        <option value="">
                            Select Complaint Type
                        </option>

                        <option value="EQUIPMENT_DAMAGE">
                            🚜 Equipment Damage
                        </option>

                        <option value="EQUIPMENT_NOT_RECEIVED">
                            📦 Equipment Not Received
                        </option>

                        <option value="WRONG_EQUIPMENT">
                            ❌ Wrong Equipment
                        </option>

                    </select>


                    <label>
                        Description
                    </label>

                    <textarea
                        id="complaint-description-${booking.id}"
                        placeholder="Describe the problem...">
                    </textarea>


                    <label>
                        Photo Filename
                    </label>

                    <input
                        type="text"
                        id="complaint-photo-${booking.id}"
                        placeholder="Example: tractor_damage.jpg"
                    >


                    <button
                        class="submit-complaint-btn"
                        onclick="
                            submitComplaint(
                                ${booking.id},
                                ${booking.equipmentId}
                            )
                        ">
                        📤 Submit Complaint
                    </button>


                    <button
                        class="cancel-complaint-btn"
                        onclick="
                            closeComplaintForm(
                                ${booking.id}
                            )
                        ">
                        Cancel
                    </button>


                    <div
                        id="complaint-status-${booking.id}">
                    </div>

                </div>

            `;


            bookingList.appendChild(card);


            // Load complaint status
            loadComplaintStatus(
                booking.id
            );
        }

    }
    catch (error) {

        console.error(
            "Booking Error:",
            error
        );

        bookingList.innerHTML = `

            <div class="error">

                ❌ Failed to load bookings.

                <br><br>

                Please make sure Spring Boot
                backend is running.

                <br><br>

                <button
                    class="refresh-btn"
                    onclick="loadBookings()">
                    🔄 Try Again
                </button>

            </div>
        `;
    }
}



// ===============================
// OPEN COMPLAINT FORM
// ===============================
function openComplaintForm(
    rentalId,
    equipmentId
) {

    const form =
        document.getElementById(
            `complaint-form-${rentalId}`
        );

    if (form) {

        form.style.display = "block";

        form.scrollIntoView({
            behavior: "smooth",
            block: "center"
        });
    }
}



// ===============================
// CLOSE COMPLAINT FORM
// ===============================
function closeComplaintForm(
    rentalId
) {

    const form =
        document.getElementById(
            `complaint-form-${rentalId}`
        );

    if (form) {
        form.style.display = "none";
    }
}



// ===============================
// SUBMIT COMPLAINT
// ===============================
async function submitComplaint(
    rentalId,
    equipmentId
) {

    const typeElement =
        document.getElementById(
            `complaint-type-${rentalId}`
        );

    const descriptionElement =
        document.getElementById(
            `complaint-description-${rentalId}`
        );

    const photoElement =
        document.getElementById(
            `complaint-photo-${rentalId}`
        );


    if (
        !typeElement ||
        !descriptionElement ||
        !photoElement
    ) {

        alert(
            "❌ Complaint form not found."
        );

        return;
    }


    const complaintType =
        typeElement.value;

    const description =
        descriptionElement.value.trim();

    const photo =
        photoElement.value.trim();


    if (!complaintType) {

        alert(
            "❌ Please select a complaint type."
        );

        return;
    }


    if (!description) {

        alert(
            "❌ Please enter the complaint description."
        );

        return;
    }


    const complaintData = {

        rentalId: Number(rentalId),

        userId: Number(userId),

        equipmentId: Number(equipmentId),

        complaintType: complaintType,

        description: description,

        photo: photo || null
    };


    console.log(
        "Complaint Data:",
        complaintData
    );


    try {

        const response =
            await fetch(
                `${API_URL}/api/complaints`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(
                            complaintData
                        )
                }
            );


        const result =
            await response.text();


        if (!response.ok) {

            alert(
                "❌ " + result
            );

            return;
        }


        alert(
            "✅ Complaint submitted successfully!"
        );


        typeElement.value = "";

        descriptionElement.value = "";

        photoElement.value = "";


        closeComplaintForm(
            rentalId
        );


        loadComplaintStatus(
            rentalId
        );

    }
    catch (error) {

        console.error(
            "Complaint Error:",
            error
        );

        alert(
            "❌ Failed to submit complaint. Please check backend."
        );
    }
}



// ===============================
// LOAD COMPLAINT STATUS
// ===============================
async function loadComplaintStatus(
    rentalId
) {

    const statusContainer =
        document.getElementById(
            `complaint-status-${rentalId}`
        );


    if (!statusContainer) {
        return;
    }


    try {

        const response =
            await fetch(
                `${API_URL}/api/complaints/rental/${rentalId}`
            );


        if (!response.ok) {
            return;
        }


        const complaints =
            await response.json();


        if (
            !complaints ||
            complaints.length === 0
        ) {

            statusContainer.innerHTML = "";

            return;
        }


        let html = "";


        complaints.forEach(
            function (complaint) {

                const statusText =
                    String(
                        complaint.status ||
                        "PENDING"
                    ).toUpperCase();


                html += `

                    <div class="complaint-status">

                        <strong>
                            📢 Complaint #${complaint.id}
                        </strong>

                        <br><br>

                        <strong>
                            Type:
                        </strong>

                        ${complaint.complaintType}

                        <br>

                        <strong>
                            Description:
                        </strong>

                        ${complaint.description}

                        <br>

                        <strong>
                            Status:
                        </strong>

                        ${statusText}

                    </div>

                `;
            }
        );


        statusContainer.innerHTML =
            html;

    }
    catch (error) {

        console.error(
            "Complaint Status Error:",
            error
        );
    }
}



// ===============================
// RATING & REVIEW
// ===============================
async function openRating(
    rentalId,
    equipmentId
) {

    try {

        const checkResponse =
            await fetch(
                `${API_URL}/api/ratings/rental/${rentalId}`
            );


        if (checkResponse.ok) {

            alert(
                "⭐ You have already reviewed this rental."
            );

            return;
        }

    }
    catch (error) {

        console.log(
            "No previous rating found."
        );
    }


    const rating =
        prompt(
            "⭐ Give rating from 1 to 5:"
        );


    if (rating === null) {
        return;
    }


    const ratingValue =
        Number(rating);


    if (
        !Number.isInteger(
            ratingValue
        ) ||
        ratingValue < 1 ||
        ratingValue > 5
    ) {

        alert(
            "❌ Please enter a rating between 1 and 5."
        );

        return;
    }


    const review =
        prompt(
            "✍️ Write your review:"
        );


    if (review === null) {
        return;
    }


    const data = {

        rentalId:
            Number(rentalId),

        userId:
            Number(userId),

        equipmentId:
            Number(equipmentId),

        rating:
            ratingValue,

        review:
            review.trim()
    };


    try {

        const response =
            await fetch(
                `${API_URL}/api/ratings`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(data)
                }
            );


        const result =
            await response.text();


        if (!response.ok) {

            alert(
                "❌ " + result
            );

            return;
        }


        alert(
            "⭐ Rating & Review submitted successfully!"
        );


        loadBookings();

    }
    catch (error) {

        console.error(
            "Rating Error:",
            error
        );

        alert(
            "❌ Failed to submit rating."
        );
    }
}



// ===============================
// NAVIGATION
// ===============================
function goBack() {

    window.location.href =
        "farmer.html";
}


function goToEquipment() {

    window.location.href =
        "equipment.html";
}



// ===============================
// PAGE LOAD
// ===============================
document.addEventListener(
    "DOMContentLoaded",
    function () {

        console.log(
            "My Bookings page loaded"
        );

        loadBookings();

    }
);