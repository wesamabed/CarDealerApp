package com.example.project;

import java.util.List;

public class Company {

    private String carDealerId;
    private String phone_number;
    private String country;
    private String city;
    private String Location;
    private String ImageUrl;
    private List<String> CarIds;
    private List<String> reservedCarIds;
    private List<String> UserreservedCar;
    private List<String> admins;

    public Company(String carDealerId, String phone_number, String country, String city, String location, String imageUrl, List<String> carIds, List<String> reservedCarIds, List<String> userreservedCar, List<String> admins) {
        this.carDealerId = carDealerId;
        this.phone_number = phone_number;
        this.country = country;
        this.city = city;
        Location = location;
        ImageUrl = imageUrl;
        CarIds = carIds;
        this.reservedCarIds = reservedCarIds;
        UserreservedCar = userreservedCar;
        this.admins = admins;
    }

    public Company() {}

    public String getcarDealerId() {
        return carDealerId;
    }

    public void setcarDealerId(String carDealerId) {
        this.carDealerId = carDealerId;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public List<String> getCarIds() {
        return CarIds;
    }

    public void setCarIds(List<String> carIds) {
        CarIds = carIds;
    }

    public List<String> getReservedCarIds() {
        return reservedCarIds;
    }

    public void setReservedCarIds(List<String> reservedCarIds) {
        this.reservedCarIds = reservedCarIds;
    }

    public List<String> getUserreservedCar() {
        return UserreservedCar;
    }

    public void setUserreservedCar(List<String> userreservedCar) {
        UserreservedCar = userreservedCar;
    }

    public List<String> getAdmins() {
        return admins;
    }

    public void setAdmins(List<String> admins) {
        this.admins = admins;
    }
}
