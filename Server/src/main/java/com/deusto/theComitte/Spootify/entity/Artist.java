package com.deusto.theComitte.Spootify.entity;

import com.deusto.theComitte.Spootify.DTO.ArtistDTO;
import jakarta.persistence.*;


@Entity
@Table(name = "Artists")
public class Artist extends GenericUser {

    public Artist(long id, String name, String username, String email, String password) {
        super(id, name, username, email, password);
    }

    public Artist() {
        super();
    }

    public ArtistDTO toDTO() {
        return new ArtistDTO(this.id, this.name);
    }
    
}
