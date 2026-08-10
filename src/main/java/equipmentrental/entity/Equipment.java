
package equipmentrental.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private double pricePerDay;
    private String location;
    private boolean available;

    // Owner ID
    private Long ownerId;

    public Equipment() {
    }

    // Get ID
    public Long getId() {
        return id;
    }

    // Set ID
    public void setId(Long id) {
        this.id = id;
    }

    // Get Name
    public String getName() {
        return name;
    }

    // Set Name
    public void setName(String name) {
        this.name = name;
    }

    // Get Category
    public String getCategory() {
        return category;
    }

    // Set Category
    public void setCategory(String category) {
        this.category = category;
    }

    // Get Price Per Day
    public double getPricePerDay() {
        return pricePerDay;
    }

    // Set Price Per Day
    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    // Get Location
    public String getLocation() {
        return location;
    }

    // Set Location
    public void setLocation(String location) {
        this.location = location;
    }

    // Get Availability
    public boolean isAvailable() {
        return available;
    }

    // Set Availability
    public void setAvailable(boolean available) {
        this.available = available;
    }

    // Get Owner ID
    public Long getOwnerId() {
        return ownerId;
    }

    // Set Owner ID
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}

