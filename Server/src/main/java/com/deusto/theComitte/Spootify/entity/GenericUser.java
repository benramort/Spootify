package com.deusto.theComitte.Spootify.entity;

import jakarta.persistence.*;

public abstract class GenericUser {
    
    @Id
    @Column(nullable = false, unique = true)
    protected long id;
    @Column(nullable = false)
    protected String name;
    @Column(nullable = false, unique = true)
    protected String email;
    @Column(nullable = false, unique = true)
    protected String password;



    public GenericUser(long id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public GenericUser() {

    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassowrd(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
