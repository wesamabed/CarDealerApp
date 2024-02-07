package com.example.project;

import java.util.List;

public class CarDealer {
    private String firstName;
    private String lastName;
    private String email;

    private String carDealerId;
    private String passwordHash;
    private String phone_number;
    private String gender;
    private String country;
    private String city;
    private String Location;
    private String ImageUrl;
    private boolean isAdmin;
    private boolean isCarDealer;
    private boolean isStaticCarDealerAdmin;



    public CarDealer(String firstName, String lastName, String email, String carDealerId,  String phone_number, String gender, String country, String city, String location, String imageUrl, boolean isAdmin, boolean isCarDealer, boolean isStaticCarDealerAdmin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.carDealerId = carDealerId;
        this.phone_number = phone_number;
        this.gender = gender;
        this.country = country;
        this.city = city;
        Location = location;
        ImageUrl = imageUrl;
        this.isAdmin = isAdmin;
        this.isCarDealer = isCarDealer;
        this.isStaticCarDealerAdmin = isStaticCarDealerAdmin;

    }
    public CarDealer() {}

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCarDealerId() {
        return carDealerId;
    }

    public void setCarDealerId(String carDealerId) {
        this.carDealerId = carDealerId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isCarDealer() {
        return isCarDealer;
    }

    public void setCarDealer(boolean carDealer) {
        isCarDealer = carDealer;
    }

    public boolean isStaticCarDealerAdmin() {
        return isStaticCarDealerAdmin;
    }

    public void setStaticCarDealerAdmin(boolean staticCarDealerAdmin) {
        isStaticCarDealerAdmin = staticCarDealerAdmin;
    }

}
