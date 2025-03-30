package com.deusto.theComitte.Spootify.entity;

import java.util.ArrayList;
import java.util.List;

import com.deusto.theComitte.Spootify.DTO.AlbumDTO;
import com.deusto.theComitte.Spootify.DTO.ArtistDTO;
import com.deusto.theComitte.Spootify.DTO.UserDTO;

import jakarta.persistence.*;


@Entity
@Table(name = "Artists")
public class Artist extends GenericUser {

    @ManyToMany(mappedBy = "artists", fetch = FetchType.EAGER)
    private List<Album> albums;
    @Column(nullable = false)
    protected long followers;    
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(
    name = "followersList",
    joinColumns = @JoinColumn(name = "artist_id"),
    inverseJoinColumns = @JoinColumn(name = "user_id")
)private List<User> followersList;

    public Artist(long id, String name, String email, String password) {
        super(id, name, email, password);
        followersList = new ArrayList<User>();
    }

    public Artist(String name, String email, String password) {
        super(name, email, password);
        followersList = new ArrayList<User>();

    }

    public Artist() {
        super();
    }

    public List<Album> getAlbums() {
        return this.albums;
    }

    public ArtistDTO toDTOWithoutAlbums() {
        return new ArtistDTO(this.id, this.name, this.followers, null,null);
    }

    public ArtistDTO toDTO() {
        List<AlbumDTO> albumDTOs = this.albums.stream().map(m -> m.toDTOWithoutArtists()).toList();
        return new ArtistDTO(this.id, this.name, this.followers, albumDTOs, null);
    }

    public ArtistDTO toDTOWithFollowers() {
        List<AlbumDTO> albumDTOs = this.albums.stream().map(m -> m.toDTOWithoutArtists()).toList();
        List<UserDTO> userDTOs = this.followersList.stream().map(m -> m.toDTOWithoutFollowing()).toList();
        return new ArtistDTO(this.id, this.name, this.followers, albumDTOs, userDTOs);
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

    public List<User> getFollowersList() {
        return followersList;
    }

    public void followArtist(User user) {
        if (!this.followersList.contains(user)) {
            this.followersList.add(user);
            user.getFollowList().add(this);
            this.setFollowers(this.getFollowers() + 1);
        }
    }
}
