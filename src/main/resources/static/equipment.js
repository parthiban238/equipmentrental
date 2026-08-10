const API_URL = "http://localhost:8081";

// ===============================
// Load Available Equipment
// ===============================
async function loadEquipment() {

    const equipmentList =
        document.getElementById("equipmentList");

    equipmentList.innerHTML =
        "<p>Loading equipment...</p>";

    try {

        const response = await fetch(
            `${API_URL}/api/equipment/available`
        );

        if (!response.ok) {
            throw new Error("Failed to fetch equipment");
        }

        const equipment = await response.json();

        console.log("Available Equipment:", equipment);

        equipmentList.innerHTML = "";

        if (equipment.length === 0) {

            equipmentList.innerHTML =
                "<p>No equipment available.</p>";

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
                    <strong>Status:</strong>
                    ${item.available
                        ? "Available"
                        : "Not Available"}
                </p>

                ${
                    item.available
                    ?
                    `<button onclick="bookEquipment(${item.id})">
                        Book Now
                    </button>`
                    :
                    `<button disabled>
                        Not Available
                    </button>`
                }
            `;

            equipmentList.appendChild(card);

        });

    } catch (error) {

        console.error("Equipment Error:", error);

        equipmentList.innerHTML = `
            <p>
                Backend connection failed.
                Please check Spring Boot.
            </p>
        `;
    }
}


// ===============================
// Book Equipment
// ===============================
async function bookEquipment(equipmentId) {

    // Get logged-in user ID
    const userId =
        localStorage.getItem("userId");

    if (!userId) {

        alert(
            "Please login first."
        );

        window.location.href =
            "/login.html";

        return;
    }


    // Get booking dates
    const startDate =
        prompt(
            "Enter Start Date (YYYY-MM-DD):"
        );

    if (!startDate) {
        return;
    }


    const endDate =
        prompt(
            "Enter End Date (YYYY-MM-DD):"
        );

    if (!endDate) {
        return;
    }


    // Booking data
    const bookingData = {

        userId: Number(userId),

        equipmentId: equipmentId,

        startDate: startDate,

        endDate: endDate
    };


    console.log(
        "Booking Data:",
        bookingData
    );


    try {

        const response = await fetch(
            `${API_URL}/api/rentals`,
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(
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
            "Booking Successful! 🎉\n\n" +
            "Rental ID: " +
            booking.id +
            "\nEquipment ID: " +
            booking.equipmentId +
            "\nStatus: " +
            booking.status
        );


        // Reload available equipment
        loadEquipment();


    } catch (error) {

        console.error(
            "Booking Error:",
            error
        );

        alert(
            "Booking failed!\n\n" +
            error.message
        );
    }
}


// ===============================
// Load Equipment on Page Open
// ===============================
document.addEventListener(
    "DOMContentLoaded",
    function() {

        loadEquipment();

    }
);