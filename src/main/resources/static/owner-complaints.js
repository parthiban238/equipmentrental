const API_URL = "https://equipmentrental-1.onrender.com";

// =====================================================
// GET OWNER LOGIN DETAILS
// =====================================================

const ownerId = localStorage.getItem("userId");
const userRole = localStorage.getItem("userRole");

console.log("Owner ID:", ownerId);
console.log("User Role:", userRole);


// =====================================================
// LOGIN + OWNER ROLE CHECK
// =====================================================

if (!ownerId) {

    alert("Please login first.");

    window.location.href = "login.html";

}

else if (
    !userRole ||
    userRole.toUpperCase() !== "OWNER"
) {

    alert("Owner access required.");

    window.location.href = "login.html";

}


// =====================================================
// LOAD OWNER COMPLAINTS
// =====================================================

async function loadComplaints() {

    const complaintList =
        document.getElementById("complaintList");

    const complaintCount =
        document.getElementById("complaintCount");


    if (!complaintList) {

        console.error(
            "complaintList element not found"
        );

        return;
    }


    complaintList.innerHTML = `
        <div class="loading">
            Loading complaints...
        </div>
    `;


    try {

        console.log(
            "Loading complaints for owner:",
            ownerId
        );


        const response = await fetch(
            `${API_URL}/api/complaints/owner/${ownerId}`
        );


        console.log(
            "Complaint API Status:",
            response.status
        );


        if (!response.ok) {

            const errorText =
                await response.text();

            throw new Error(
                errorText ||
                `Server error: ${response.status}`
            );
        }


        const complaints =
            await response.json();


        console.log(
            "Owner Complaints:",
            complaints
        );


        // =================================================
        // COMPLAINT COUNT
        // =================================================

        if (complaintCount) {

            complaintCount.textContent =
                `Complaints: ${complaints.length}`;

        }


        // =================================================
        // NO COMPLAINTS
        // =================================================

        if (
            !Array.isArray(complaints) ||
            complaints.length === 0
        ) {

            complaintList.innerHTML = `

                <div class="empty">

                    <h3>
                        ✅ No Complaints Found
                    </h3>

                    <p>
                        There are no complaints
                        reported for your equipment.
                    </p>

                </div>

            `;

            return;
        }


        // =================================================
        // CLEAR LIST
        // =================================================

        complaintList.innerHTML = "";


        // =================================================
        // DISPLAY EACH COMPLAINT
        // =================================================

        complaints.forEach(
            function (complaint) {

                const card =
                    document.createElement("div");


                card.className =
                    "complaint-card";


                // -----------------------------------------
                // STATUS
                // -----------------------------------------

                const status =
                    String(
                        complaint.status ||
                        "PENDING"
                    ).toUpperCase();


                let statusClass =
                    "status-pending";


                if (
                    status === "UNDER_REVIEW"
                ) {

                    statusClass =
                        "status-under-review";

                }

                else if (
                    status === "RESOLVED"
                ) {

                    statusClass =
                        "status-resolved";

                }

                else if (
                    status === "REJECTED"
                ) {

                    statusClass =
                        "status-rejected";

                }


                // -----------------------------------------
                // COMPLAINT TYPE
                // -----------------------------------------

                const complaintType =
                    formatComplaintType(
                        complaint.complaintType
                    );


                // -----------------------------------------
                // DATE
                // -----------------------------------------

                const createdDate =
                    formatDate(
                        complaint.createdAt
                    );


                // -----------------------------------------
                // PHOTO
                // -----------------------------------------

                const photo =
                    complaint.photo
                    ? complaint.photo
                    : "No photo provided";


                // -----------------------------------------
                // CARD HTML
                // -----------------------------------------

                card.innerHTML = `

                    <div class="complaint-header">

                        <h3>
                            📢 Complaint #${escapeHtml(
                                complaint.id
                            )}
                        </h3>

                        <span
                            class="status ${statusClass}">

                            ${escapeHtml(status)}

                        </span>

                    </div>


                    <div class="complaint-type">

                        ⚠️ ${escapeHtml(
                            complaintType
                        )}

                    </div>


                    <div class="details">


                        <div class="detail-box">

                            <strong>
                                📋 Rental ID
                            </strong>

                            <span>
                                ${escapeHtml(
                                    complaint.rentalId
                                )}
                            </span>

                        </div>


                        <div class="detail-box">

                            <strong>
                                👨‍🌾 Farmer ID
                            </strong>

                            <span>
                                ${escapeHtml(
                                    complaint.userId
                                )}
                            </span>

                        </div>


                        <div class="detail-box">

                            <strong>
                                🚜 Equipment ID
                            </strong>

                            <span>
                                ${escapeHtml(
                                    complaint.equipmentId
                                )}
                            </span>

                        </div>


                        <div class="detail-box">

                            <strong>
                                📅 Reported Date
                            </strong>

                            <span>
                                ${escapeHtml(
                                    createdDate
                                )}
                            </span>

                        </div>


                    </div>


                    <div class="description-box">

                        <strong>
                            📝 Complaint Description
                        </strong>

                        <p>

                            ${escapeHtml(
                                complaint.description ||
                                "No description provided"
                            )}

                        </p>

                    </div>


                    <div class="photo-box">

                        📷 <strong>Photo:</strong>

                        ${escapeHtml(photo)}

                    </div>


                    <div class="status-control">

                        <label
                            for="status-${complaint.id}">

                            🔄 Update Complaint Status

                        </label>


                        <select
                            id="status-${complaint.id}">

                            <option
                                value="PENDING"
                                ${
                                    status === "PENDING"
                                    ? "selected"
                                    : ""
                                }>

                                PENDING

                            </option>


                            <option
                                value="UNDER_REVIEW"
                                ${
                                    status === "UNDER_REVIEW"
                                    ? "selected"
                                    : ""
                                }>

                                UNDER REVIEW

                            </option>


                            <option
                                value="RESOLVED"
                                ${
                                    status === "RESOLVED"
                                    ? "selected"
                                    : ""
                                }>

                                RESOLVED

                            </option>


                            <option
                                value="REJECTED"
                                ${
                                    status === "REJECTED"
                                    ? "selected"
                                    : ""
                                }>

                                REJECTED

                            </option>

                        </select>


                        <button
                            class="update-btn"
                            id="update-btn-${complaint.id}"
                            onclick="updateComplaintStatus(${complaint.id})">

                            🔄 Update Status

                        </button>

                    </div>

                `;


                complaintList.appendChild(card);

            }
        );

    }


    catch (error) {

        console.error(
            "Complaint Loading Error:",
            error
        );


        if (complaintCount) {

            complaintCount.textContent =
                "Complaints: 0";

        }


        complaintList.innerHTML = `

            <div class="error">

                ❌ <strong>
                    Failed to load complaints.
                </strong>

                <br><br>

                ${escapeHtml(
                    error.message ||
                    "Unable to connect to server."
                )}

                <br><br>

                <button
                    class="refresh-btn"
                    onclick="loadComplaints()">

                    🔄 Try Again

                </button>

            </div>

        `;

    }

}


// =====================================================
// UPDATE COMPLAINT STATUS
// =====================================================

async function updateComplaintStatus(
    complaintId
) {

    const select =
        document.getElementById(
            `status-${complaintId}`
        );


    const button =
        document.getElementById(
            `update-btn-${complaintId}`
        );


    if (!select) {

        alert(
            "❌ Status selector not found."
        );

        return;
    }


    const newStatus =
        select.value;


    if (!newStatus) {

        alert(
            "❌ Please select a status."
        );

        return;
    }


    // =================================================
    // CONFIRMATION
    // =================================================

    const confirmed =
        confirm(
            `Change complaint #${complaintId} status to ${newStatus}?`
        );


    if (!confirmed) {

        return;
    }


    // =================================================
    // DISABLE BUTTON
    // =================================================

    if (button) {

        button.disabled = true;

        button.textContent =
            "Updating...";

    }


    try {

        console.log(
            "Updating complaint:",
            complaintId,
            "Status:",
            newStatus
        );


        const response =
            await fetch(
                `${API_URL}/api/complaints/${complaintId}/status`,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        status: newStatus
                    })
                }
            );


        const result =
            await response.text();


        console.log(
            "Update Response:",
            result
        );


        if (!response.ok) {

            throw new Error(
                result ||
                `Failed to update status. HTTP ${response.status}`
            );

        }


        alert(
            `✅ Complaint #${complaintId} updated to ${newStatus}`
        );


        // Reload complaints

        await loadComplaints();

    }


    catch (error) {

        console.error(
            "Status Update Error:",
            error
        );


        alert(
            "❌ " +
            (
                error.message ||
                "Failed to update complaint status."
            )
        );


        if (button) {

            button.disabled = false;

            button.textContent =
                "🔄 Update Status";

        }

    }

}


// =====================================================
// FORMAT COMPLAINT TYPE
// =====================================================

function formatComplaintType(
    type
) {

    if (!type) {

        return "Unknown";

    }


    switch (
        String(type).toUpperCase()
    ) {

        case "EQUIPMENT_DAMAGE":

            return "Equipment Damage";


        case "EQUIPMENT_NOT_RECEIVED":

            return "Equipment Not Received";


        case "WRONG_EQUIPMENT":

            return "Wrong Equipment";


        default:

            return type;

    }

}


// =====================================================
// FORMAT DATE
// =====================================================

function formatDate(
    dateString
) {

    if (!dateString) {

        return "Not available";

    }


    try {

        const date =
            new Date(dateString);


        if (
            Number.isNaN(
                date.getTime()
            )
        ) {

            return dateString;

        }


        return date.toLocaleString(
            "en-IN",
            {
                day: "2-digit",

                month: "short",

                year: "numeric",

                hour: "2-digit",

                minute: "2-digit"
            }
        );

    }


    catch (error) {

        return dateString;

    }

}


// =====================================================
// HTML ESCAPE
// =====================================================

function escapeHtml(
    value
) {

    if (
        value === null ||
        value === undefined
    ) {

        return "";

    }


    return String(value)

        .replace(
            /&/g,
            "&amp;"
        )

        .replace(
            /</g,
            "&lt;"
        )

        .replace(
            />/g,
            "&gt;"
        )

        .replace(
            /"/g,
            "&quot;"
        )

        .replace(
            /'/g,
            "&#039;"
        );

}


// =====================================================
// PAGE LOAD
// =====================================================

document.addEventListener(
    "DOMContentLoaded",
    function () {

        console.log(
            "================================"
        );

        console.log(
            "Owner Complaints Page Loaded"
        );

        console.log(
            "Owner ID:",
            ownerId
        );

        console.log(
            "Role:",
            userRole
        );

        console.log(
            "================================"
        );


        if (
            ownerId &&
            userRole &&
            userRole.toUpperCase() === "OWNER"
        ) {

            loadComplaints();

        }

    }
);