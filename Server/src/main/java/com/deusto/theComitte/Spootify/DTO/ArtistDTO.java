package com.deusto.theComitte.Spootify.DTO;

public class ArtistDTO {
    
    private long id;
    private String name;
    private long followers;

    public ArtistDTO() {
    }

    public ArtistDTO(long id, String name,long followers) {
        this.id = id;
        this.name = name;
        this.followers = followers;
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

}
