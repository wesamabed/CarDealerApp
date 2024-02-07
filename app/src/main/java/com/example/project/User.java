package com.example.project;

import java.util.List;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private String phone_number;
    private String gender;
    private String country;
    private String city;
    private String ImageUrl;
    private boolean isAdmin;
    private boolean isCarDealer;
    private List<String> favoriteCarIds;
    private List<String> reservedCarIds;

    public User(String firstName, String lastName, String email, String phone_number, String gender, String country, String city, String ImageUrl,  boolean isAdmin, boolean isCarDealer, List<String> favoriteCarIds, List<String> reservedCarIds) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone_number = phone_number;
        this.gender = gender;
        this.country = country;
        this.city = city;
        this.ImageUrl = ImageUrl;
        this.isAdmin = isAdmin;
        this.isCarDealer = isCarDealer;
        this.favoriteCarIds = favoriteCarIds;
        this.reservedCarIds = reservedCarIds;

    }
    public User() {}

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

    public List<String> getFavoriteCarIds() {
        return favoriteCarIds;
    }

    public void setFavoriteCarIds(List<String> favoriteCarIds) {
        this.favoriteCarIds = favoriteCarIds;
    }

    public List<String> getReservedCarIds() {
        return reservedCarIds;
    }

    public void setReservedCarIds(List<String> reservedCarIds) {
        this.reservedCarIds = reservedCarIds;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", gender='" + gender + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", isAdmin=" + isAdmin +
                ", isCarDealer=" + isCarDealer +
                ", favoriteCarIds=" + favoriteCarIds +
                ", reservedCarIds=" + reservedCarIds +
                '}';
    }
}
