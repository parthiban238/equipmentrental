const API_URL = "https://equipmentrental-1.onrender.com";

// =====================================================
// Date Validation - Today and Future Only
// =====================================================
function setupDateValidation() {

    const startDate = document.getElementById("startDate");
    const endDate = document.getElementById("endDate");

    if (!startDate || !endDate) {
        return;
    }

    const today = new Date().toISOString().split("T")[0];

    startDate.min = today;
    endDate.min = today;

    startDate.addEventListener("change", function () {

        if (this.value) {

            endDate.min = this.value;

            if (endDate.value && endDate.value < this.value) {
                endDate.value = this.value;
            }
        }
    });
}


// =====================================================
// Load Equipment
// =====================================================
async function loadEquipment() {

    const equipmentList =
        document.getElementById("equipmentList");

    equipmentList.innerHTML =
        "<p>Loading equipment...</p>";

    try {

        const response = await fetch(
            `${API_URL}/api/equipment`
        );

        if (!response.ok) {
            throw new Error("Failed to fetch equipment");
        }

        const equipment =
            await response.json();

        console.log("Equipment:", equipment);

        displayEquipment(equipment);

    } catch (error) {

        console.error("Equipment Error:", error);

        equipmentList.innerHTML = `
            <p style="color:red;">
                ❌ Backend connection failed.
                <br>
                Please check the backend.
            </p>
        `;
    }
}


// =====================================================
// Display Equipment
// =====================================================
function displayEquipment(equipment) {

    const equipmentList =
        document.getElementById("equipmentList");

    equipmentList.innerHTML = "";

    equipmentList.style.display = "grid";
    equipmentList.style.gridTemplateColumns =
        "repeat(3, 1fr)";
    equipmentList.style.gap = "25px";
    equipmentList.style.padding = "25px 0";
    equipmentList.style.width = "100%";
    equipmentList.style.boxSizing = "border-box";


    if (equipment.length === 0) {

        equipmentList.style.display = "block";

        equipmentList.innerHTML = `
            <p>
                ❌ No equipment found.
            </p>
        `;

        return;
    }


    equipment.forEach(item => {

        const card =
            document.createElement("div");

        card.className = "equipment-card";


        card.style.background = "white";
        card.style.padding = "20px";
        card.style.borderRadius = "12px";
        card.style.boxShadow =
            "0 4px 12px rgba(0,0,0,0.15)";
        card.style.border = "1px solid #ddd";
        card.style.boxSizing = "border-box";
        card.style.minHeight = "240px";


        card.innerHTML = `

            <h3 style="
                margin-top:0;
                margin-bottom:12px;
                font-size:21px;
            ">
                🚜 ${item.name}
            </h3>

            <p style="margin:8px 0;">
                <strong>Category:</strong>
                ${item.category}
            </p>

            <p style="margin:8px 0;">
                <strong>Location:</strong>
                ${item.location}
            </p>

            <p style="margin:8px 0;">
                <strong>Price:</strong>
                ₹${item.pricePerDay} / day
            </p>

            <p style="margin:8px 0;">
                <strong>Current Status:</strong>

                ${
                    item.available
                    ?
                    `<span style="color:green;">
                        🟢 Available
                    </span>`
                    :
                    `<span style="color:red;">
                        🔴 Currently Unavailable
                    </span>`
                }

            </p>


            <div class="equipment-buttons">

                ${
                    item.available
                    ?
                    `
                    <button
                        onclick="checkAvailability(${item.id})"
                        style="
                            margin-top:12px;
                            padding:9px 15px;
                            border:none;
                            border-radius:6px;
                            cursor:pointer;
                        "
                    >
                        📅 Check Availability
                    </button>
                    `
                    :
                    `
                    <button
                        disabled
                        style="
                            margin-top:12px;
                            padding:9px 15px;
                            border:none;
                            border-radius:6px;
                        "
                    >
                        🔴 Currently Unavailable
                    </button>
                    `
                }

            </div>


            <div id="availability-${item.id}"></div>

        `;


        equipmentList.appendChild(card);

    });

}


// =====================================================
// Search Equipment
// =====================================================
async function searchEquipment() {

    const searchText =
        document.getElementById("searchInput")
            .value
            .trim()
            .toLowerCase();


    const category =
        document.getElementById("categoryFilter")
            .value
            .toLowerCase();


    try {

        const response = await fetch(
            `${API_URL}/api/equipment`
        );

        if (!response.ok) {
            throw new Error("Failed to load equipment");
        }

        const equipment =
            await response.json();


        const filtered =
            equipment.filter(item => {

                const name =
                    (item.name || "")
                    .toLowerCase();

                const itemCategory =
                    (item.category || "")
                    .toLowerCase();

                const location =
                    (item.location || "")
                    .toLowerCase();


                const matchesSearch =
                    searchText === "" ||
                    name.includes(searchText) ||
                    itemCategory.includes(searchText) ||
                    location.includes(searchText);


                const matchesCategory =
                    category === "" ||
                    itemCategory === category;


                return matchesSearch &&
                       matchesCategory;

            });


        displayEquipment(filtered);


    } catch (error) {

        console.error(
            "Search Error:",
            error
        );


        alert(
            "Unable to search equipment."
        );
    }
}


// =====================================================
// Check Equipment Availability
// =====================================================
async function checkAvailability(equipmentId) {

    const startDate =
        document.getElementById("startDate")
            .value;


    const endDate =
        document.getElementById("endDate")
            .value;


    if (!startDate || !endDate) {

        alert(
            "Please select Start Date and End Date."
        );

        return;
    }


    const today =
        new Date().toISOString().split("T")[0];


    if (startDate < today) {

        alert(
            "❌ Start Date cannot be in the past."
        );

        return;
    }


    if (endDate < today) {

        alert(
            "❌ End Date cannot be in the past."
        );

        return;
    }


    if (endDate < startDate) {

        alert(
            "End Date cannot be before Start Date."
        );

        return;
    }


    const resultDiv =
        document.getElementById(
            `availability-${equipmentId}`
        );


    resultDiv.innerHTML =
        "<p>⏳ Checking availability...</p>";


    try {

        const response = await fetch(

            `${API_URL}/api/rentals/availability` +
            `?equipmentId=${equipmentId}` +
            `&startDate=${startDate}` +
            `&endDate=${endDate}`

        );


        const result =
            await response.text();


        console.log(
            "Availability Result:",
            result
        );


        if (
            result.toLowerCase()
                .includes("available")
            &&
            !result.toLowerCase()
                .includes("not available")
            &&
            !result.toLowerCase()
                .includes("already booked")
        ) {

            resultDiv.innerHTML = `

                <div style="
                    margin-top:10px;
                    padding:10px;
                    border:1px solid green;
                    border-radius:5px;
                ">

                    <p style="color:green;">
                        🟢 ${result}
                    </p>

                    <button
                        onclick="bookEquipment(
                            ${equipmentId},
                            '${startDate}',
                            '${endDate}'
                        )"
                    >
                        ✅ Book Now
                    </button>

                </div>

            `;

        } else {

            resultDiv.innerHTML = `

                <div style="
                    margin-top:10px;
                    padding:10px;
                    border:1px solid red;
                    border-radius:5px;
                ">

                    <p style="color:red;">
                        🔴 ${result}
                    </p>

                </div>

            `;
        }


    } catch (error) {

        console.error(
            "Availability Error:",
            error
        );


        resultDiv.innerHTML = `

            <p style="color:red;">
                ❌ Unable to check availability.
                Please check backend.
            </p>

        `;
    }
}


// =====================================================
// Book Equipment
// =====================================================
async function bookEquipment(
    equipmentId,
    startDate,
    endDate
) {

    const userId =
        localStorage.getItem("userId");


    if (!userId) {

        alert(
            "Please login first."
        );

        window.location.href =
            "login.html";

        return;
    }


    const today =
        new Date().toISOString().split("T")[0];


    if (startDate < today) {

        alert(
            "❌ Booking cannot be made for a past date."
        );

        return;
    }


    if (endDate < today) {

        alert(
            "❌ End date cannot be in the past."
        );

        return;
    }


    if (endDate < startDate) {

        alert(
            "❌ End Date cannot be before Start Date."
        );

        return;
    }


    const bookingData = {

        userId:
            Number(userId),

        equipmentId:
            equipmentId,

        startDate:
            startDate,

        endDate:
            endDate

    };


    console.log(
        "Booking Data:",
        bookingData
    );


    try {

        const response =
            await fetch(
                `${API_URL}/api/rentals`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(
                            bookingData
                        )
                }
            );


        if (!response.ok) {

            const errorMessage =
                await response.text();

            throw new Error(
                errorMessage ||
                "Booking failed"
            );
        }


        const booking =
            await response.json();


        console.log(
            "Booking:",
            booking
        );


        alert(

            "🎉 Booking Successful!\n\n" +

            "Rental ID: " +
            booking.id +

            "\nEquipment ID: " +
            booking.equipmentId +

            "\nStart Date: " +
            booking.startDate +

            "\nEnd Date: " +
            booking.endDate +

            "\nTotal Amount: ₹" +
            booking.totalAmount +

            "\nStatus: " +
            booking.status

        );


        loadEquipment();


    } catch (error) {

        console.error(
            "Booking Error:",
            error
        );


        alert(
            "❌ Booking Failed!\n\n" +
            error.message
        );
    }
}


// =====================================================
// Search while typing
// =====================================================
document
    .getElementById("searchInput")
    ?.addEventListener(
        "input",
        searchEquipment
    );


// =====================================================
// Load Equipment on Page Open
// =====================================================
document.addEventListener(
    "DOMContentLoaded",
    function() {

        loadEquipment();

        setupDateValidation();

    }
);