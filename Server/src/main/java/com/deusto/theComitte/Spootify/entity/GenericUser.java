package com.deusto.theComitte.Spootify.entity;

public abstract class GenericUser {
    
    protected long id;
    protected String name;
    protected String email;
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
