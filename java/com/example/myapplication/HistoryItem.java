package com.example.myapplication;

import com.google.firebase.Timestamp;
import java.io.Serializable;

public class HistoryItem implements Serializable {
    private String id;
    private Timestamp timestamp;  // Use Timestamp for date/time
    private double totalEmission;
    private double transportationEmission;
    private double shoppingEmission;
    private double energyEmission;
    private double foodEmission;


    private String transportation;
    private String shopping;
    private String energy;
    private String food;

    public HistoryItem() {}

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public double getTotalEmission() { return totalEmission; }
    public void setTotalEmission(double totalEmission) { this.totalEmission = totalEmission; }

    public double getTransportationEmission() { return transportationEmission; }
    public void setTransportationEmission(double transportationEmission) { this.transportationEmission = transportationEmission; }

    public double getShoppingEmission() { return shoppingEmission; }
    public void setShoppingEmission(double shoppingEmission) { this.shoppingEmission = shoppingEmission; }

    public double getEnergyEmission() { return energyEmission; }
    public void setEnergyEmission(double energyEmission) { this.energyEmission = energyEmission; }

    public double getFoodEmission() { return foodEmission; }
    public void setFoodEmission(double foodEmission) { this.foodEmission = foodEmission; }

    public String getTransportation() { return transportation; }
    public void setTransportation(String transportation) { this.transportation = transportation; }

    public String getShopping() { return shopping; }
    public void setShopping(String shopping) { this.shopping = shopping; }

    public String getEnergy() { return energy; }
    public void setEnergy(String energy) { this.energy = energy; }

    public String getFood() { return food; }
    public void setFood(String food) { this.food = food; }
}
