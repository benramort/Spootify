package com.deusto.theComitte.Spootify.DTO;

import java.util.List;

public class ArtistDTO {
    
    private long id;
    private String name;
    private List<AlbumDTO> albums;
    private List<UserDTO> followersList;

    public ArtistDTO() {
    }

    public ArtistDTO(long id, String name, List<AlbumDTO> albums,List<UserDTO> followersList) {
        this.id = id;
        this.name = name;
        this.albums = albums;
        this.followersList= followersList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AlbumDTO> getAlbums() {
        return albums;
    }

    public void setAlbums(List<AlbumDTO> albums) {
        this.albums = albums;
    }

    public List<UserDTO> getFollowersList() {
        return followersList;
    }
    
    public void setFollowersList(List<UserDTO> followersList) {
        this.followersList = followersList;
    }

}
