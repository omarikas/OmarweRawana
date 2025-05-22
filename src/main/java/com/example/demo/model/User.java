package com.example.demo.model;

import jakarta.persistence.*;


@Entity
@Table(name = "users")

public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String name;
    private String address;
    private String preferences;
    private boolean isValidated;

    public User () {} // Required by JPA

    // Fluent setters
    public User  withEmailandpass(String email,String password) {
        this.email = email;
        this.password=password;
        return this;
    }

    

    public User  withName(String name) {
        this.name = name;
        return this;
    }

    public User  withAddress(String address) {
        this.address = address;
        return this;
    }

    public User  withPreferences(String preferences) {
        this.preferences = preferences;
        return this;
    }

    public void validateEmail() {
        this.isValidated = true;
    }

    public boolean isValidated() {
        return isValidated;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public void setValidated(boolean isValidated) {
        this.isValidated = isValidated;
    }
}
