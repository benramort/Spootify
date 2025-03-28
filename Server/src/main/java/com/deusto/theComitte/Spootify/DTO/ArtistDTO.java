package com.deusto.theComitte.Spootify.DTO;

import java.util.List;

public class ArtistDTO {
    
    private long id;
    private String name;
    private long followers;
    private List<AlbumDTO> albums;

    public ArtistDTO() {
    }

    public ArtistDTO(long id, String name,long followers, List<AlbumDTO> albums) {
        this.id = id;
        this.name = name;
        this.followers = followers;
        this.albums = albums;
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

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getFollowers() {
        return followers;
    }

    public List<AlbumDTO> getAlbums() {
        return albums;
    }

    public void setAlbums(List<AlbumDTO> albums) {
        this.albums = albums;
    }

}
