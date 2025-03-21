package com.deusto.theComitte.Spootify.entity;

import java.util.List;

import com.deusto.theComitte.Spootify.DTO.ArtistDTO;
import jakarta.persistence.*;


@Entity
@Table(name = "Artists")
public class Artist extends GenericUser {

    @ManyToMany
    private List<Album> albums;

    public Artist(long id, String name, String email, String password) {
        super(id, name, email, password);
    }

    public Artist(String name, String email, String password) {
        super(name, email, password);
    }

    public Artist() {
        super();
    }

    public List<Album> getAlbums() {
        return this.albums;
    }

    public ArtistDTO toDTO() {
        return new ArtistDTO(this.id, this.name);
    }
    
}
