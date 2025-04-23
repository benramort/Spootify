package com.deusto.theComitte.Spootify.entity;

import java.io.Serializable;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class GenericUser implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    protected long id;
    @Column(nullable = false)
    protected String name;
    @Column(nullable = false, unique = true)
    protected String email;
    @Column(nullable = false)
    protected String password;



    public GenericUser(long id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public GenericUser(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public GenericUser() {

    }

    public void setId(long id) {
        this.id = id;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
