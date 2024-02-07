package com.example.project;
import java.io.Serializable;
public class CarType implements Serializable {
    private String id;
    private String type;
    private String name;
    private String model;
    private double price;
    private String imageUrl;
    private String fuelType;
    private int kilometers;
    private boolean isReserved;
    private String reservedto;
    private boolean isSpecialOffers;
    private String reservationFrom;
    private String reservationTo;
    private String carDealerId;

    public CarType(String id, String type, String name, String model, double price, String imageUrl, String fuelType, int kilometers, boolean isReserved,String reservedto, boolean isSpecialOffers, String reservationFrom, String reservationTo, String carDealerId) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.model = model;
        this.price = price;
        this.imageUrl = imageUrl;
        this.fuelType = fuelType;
        this.kilometers = kilometers;
        this.isReserved = isReserved;
        this.reservedto = reservedto;
        this.isSpecialOffers = isSpecialOffers;
        this.reservationFrom = reservationFrom;
        this.reservationTo = reservationTo;
        this.carDealerId = carDealerId;
    }

    public String getReservedto() {
        return reservedto;
    }

    public void setReservedto(String reservedto) {
        this.reservedto = reservedto;
    }

    public boolean isSpecialOffers() {
        return isSpecialOffers;
    }

    public void setSpecialOffers(boolean specialOffers) {
        isSpecialOffers = specialOffers;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public int getKilometers() {
        return kilometers;
    }

    public void setKilometers(int kilometers) {
        this.kilometers = kilometers;
    }

    public boolean isReserved() {
        return isReserved;
    }
    public boolean getisReserved() {
        return isReserved;
    }
    public void isReserved(boolean reserved) {
        isReserved = reserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }
    public void setisReserved(boolean reserved) {
        isReserved = reserved;
    }

    public String getReservationFrom() {
        return reservationFrom;
    }

    public void setReservationFrom(String reservationFrom) {
        this.reservationFrom = reservationFrom;
    }

    public String getReservationTo() {
        return reservationTo;
    }

    public void setReservationTo(String reservationTo) {
        this.reservationTo = reservationTo;
    }

    public String getCarDealerId() {
        return carDealerId;
    }

    public void setCarDealerId(String carDealerId) {
        this.carDealerId = carDealerId;
    }

    public CarType(String id, String type, String name, String model, double price, String imageUrl) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.model = model;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public CarType(String id, String type) {
        this.id = id;
        this.type = type;
    }
    public CarType() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
