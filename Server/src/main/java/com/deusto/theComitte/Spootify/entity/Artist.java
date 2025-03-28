package com.deusto.theComitte.Spootify.entity;

import java.util.List;

import com.deusto.theComitte.Spootify.DTO.AlbumDTO;
import com.deusto.theComitte.Spootify.DTO.ArtistDTO;
import jakarta.persistence.*;


@Entity
@Table(name = "Artists")
public class Artist extends GenericUser {

    @ManyToMany(mappedBy = "artists", fetch = FetchType.EAGER)
    private List<Album> albums;
    @Column(nullable = false)
    protected long followers;

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

    public ArtistDTO toDTOWithoutAlbums() {
        return new ArtistDTO(this.id, this.name, this.followers, null);
    }

    public ArtistDTO toDTO() {
        List<AlbumDTO> albumDTOs = this.albums.stream().map(m -> m.toDTOWithoutArtists()).toList();
        return new ArtistDTO(this.id, this.name, this.followers, albumDTOs);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Artist artist = (Artist) obj;
        return this.id == artist.id;
    }
    
    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }
}
