package com.deusto.theComitte.Spootify.entity;

import java.util.List;

import com.deusto.theComitte.Spootify.DTO.ArtistDTO;
import jakarta.persistence.*;


@Entity
@Table(name = "Artists")
public class Artist extends GenericUser {

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Song> songs;

    public Artist(long id, String name, String email, String password) {
        super(id, name, email, password);
    }

    public Artist(String name, String email, String password) {
        super(name, email, password);
    }

    public Artist() {
        super();
    }

    public List<Song> getSongs() {
        return this.songs;
    }

    public ArtistDTO toDTO() {
        return new ArtistDTO(this.id, this.name);
    }
    
}
