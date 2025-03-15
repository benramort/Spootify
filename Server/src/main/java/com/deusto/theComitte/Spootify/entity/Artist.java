package com.deusto.theComitte.Spootify.entity;

public class Artist extends GenericUser {
    
    public Artist(long id, String name, String email, String password) {
        super(id, name, email, password);
    }

    public Artist() {
        super();
    }
    
}
