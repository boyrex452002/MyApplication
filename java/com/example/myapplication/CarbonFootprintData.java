package com.example.myapplication;

import java.io.Serializable;

public class CarbonFootprintData implements Serializable {
    private double transportationEmission;
    private double energyEmission;
    private double shoppingEmission;
    private double foodEmission;
    private double totalEmission;

    public CarbonFootprintData() {}

    public CarbonFootprintData(double transportationEmission, double energyEmission, double shoppingEmission, double foodEmission) {
        this.transportationEmission = transportationEmission;
        this.energyEmission = energyEmission;
        this.shoppingEmission = shoppingEmission;
        this.foodEmission = foodEmission;
        calculateTotalEmission();
    }

    public void calculateTotalEmission() {
        this.totalEmission = transportationEmission + energyEmission + shoppingEmission + foodEmission;
    }

    public double getTransportationEmission() { return transportationEmission; }
    public void setTransportationEmission(double transportationEmission) { this.transportationEmission = transportationEmission; }

    public double getEnergyEmission() { return energyEmission; }
    public void setEnergyEmission(double energyEmission) { this.energyEmission = energyEmission; }

    public double getShoppingEmission() { return shoppingEmission; }
    public void setShoppingEmission(double shoppingEmission) { this.shoppingEmission = shoppingEmission; }

    public double getFoodEmission() { return foodEmission; }
    public void setFoodEmission(double foodEmission) { this.foodEmission = foodEmission; }

    public double getTotalEmission() { return totalEmission; }
    public void setTotalEmission(double totalEmission) { this.totalEmission = totalEmission; }
}
