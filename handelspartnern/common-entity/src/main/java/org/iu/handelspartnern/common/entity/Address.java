package org.iu.handelspartnern.common.entity;

public class Address {
    private String street;
    private String city;
    private String zipCode;
    private String country;
    private String type;

    public Address() {
    }

    public Address(String street, String city, String zipCode, String country, String type) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Address{" + "street='" + street + '\'' + ", city='" + city + '\'' + ", zipCode='" + zipCode + '\''
                + ", country='" + country + '\'' + ", type='" + type + '\'' + '}';
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}