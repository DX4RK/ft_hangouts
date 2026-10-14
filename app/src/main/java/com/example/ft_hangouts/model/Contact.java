package com.example.ft_hangouts.model;

public class Contact {
    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String address;

    public Contact(int id, String firstName, String lastName,
                   String phone, String email, String address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public Contact(String firstName, String lastName,
                   String phone, String email, String address) {
        this(0, firstName, lastName, phone, email, address);
    }

    public int getId() { return this.id; }
    public String getFirstName() { return this.firstName; }
    public String getLastName() { return this.lastName; }
    public String getPhone() { return this.phone; }
    public String getEmail() { return this.email; }
    public String getAddress() { return this.address; }

    public void setId(int id) { this.id = id; }
    public void setFirstName(String name) { this.firstName = name; }
    public void setLastName(String name) { this.lastName = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
}