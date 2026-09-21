const API_URL =
    "https://equipmentrental-1.onrender.com";

// ==========================================
// Get Available Equipment
// ==========================================

async function browseEquipment() {

    try {

        const response = await fetch(
            `${API_URL}/api/equipment/available`
        );

        if (!response.ok) {
            throw new Error("Failed to fetch equipment");
        }

        const equipment = await response.json();

        console.log("Available Equipment:", equipment);

        const equipmentList =
            document.getElementById("equipmentList");

        equipmentList.innerHTML = "";

        if (equipment.length === 0) {

            equipmentList.innerHTML =
                "<p>No equipment available.</p>";

            return;
        }

        equipment.forEach(item => {

            const card = document.createElement("div");

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
                    Available
                </p>

                <button onclick="bookEquipment(${item.id})">
                    Book Now
                </button>
            `;

            equipmentList.appendChild(card);

        });

    } catch (error) {

        console.error(
            "Equipment Error:",
            error
        );

        alert(
            "Backend connection failed. " +
            "Please check Spring Boot."
        );
    }
}


// ==========================================
// Open Booking
// ==========================================

function bookEquipment(equipmentId) {

    const userId = prompt(
        "Enter User ID:"
    );

    if (!userId) {
        return;
    }


    const startDate = prompt(
        "Enter Start Date (YYYY-MM-DD):"
    );

    if (!startDate) {
        return;
    }


    const endDate = prompt(
        "Enter End Date (YYYY-MM-DD):"
    );

    if (!endDate) {
        return;
    }


    createRental(
        Number(userId),
        equipmentId,
        startDate,
        endDate
    );
}


// ==========================================
// Create Rental
// ==========================================

async function createRental(
    userId,
    equipmentId,
    startDate,
    endDate
) {

    try {

        const response = await fetch(
            `${API_URL}/api/rentals`,
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({

                    userId: userId,

                    equipmentId: equipmentId,

                    startDate: startDate,

                    endDate: endDate

                })
            }
        );


        // Check response
        if (!response.ok) {

            const errorText =
                await response.text();

            throw new Error(
                errorText ||
                "Rental creation failed"
            );
        }


        // Get rental response
        const rental =
            await response.json();


        console.log(
            "Rental Created:",
            rental
        );


        // Success message
        alert(
            "Booking successful!\n\n" +

            "Rental ID: " +
            rental.id +

            "\nTotal Amount: ₹" +
            rental.totalAmount +

            "\nStatus: " +
            rental.status
        );


    } catch (error) {

        console.error(
            "Booking Error:",
            error
        );


        alert(
            "Booking failed:\n\n" +
            error.message
        );
    }
}