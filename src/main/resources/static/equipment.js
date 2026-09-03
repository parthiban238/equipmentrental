const API_URL = "http://localhost:8081";


// =====================================================
// Load Available Equipment
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
                Please check Spring Boot.
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

    if (equipment.length === 0) {

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


        card.innerHTML = `

            <h3>🚜 ${item.name}</h3>

            <p>
                <strong>Category:</strong>
                ${item.category}
            </p>

            <p>
                <strong>Location:</strong>
                ${item.location}
            </p>

            <p>
                <strong>Price:</strong>
                ₹${item.pricePerDay} / day
            </p>

            <p>
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
                        onclick="checkAvailability(${item.id})">
                        📅 Check Availability
                    </button>
                    `
                    :
                    `
                    <button disabled>
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


    // Validate dates
    if (!startDate || !endDate) {

        alert(
            "Please select Start Date and End Date."
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
                        )">

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


        // Refresh equipment
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

    }
);