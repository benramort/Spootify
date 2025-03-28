package com.deusto.theComitte.Spootify.DTO;

import java.util.List;

public class UserDTO {
    private long id;
    private String name;
    private List<ArtistDTO> userFollows;

    public UserDTO() {
    }

    public UserDTO(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserDTO(long id, String name, List<UserDTO> userFriends, List<SongDTO> userSongs) {
        this.id = id;
        this.name = name;

    }

    public UserDTO(long id, String name,  List<ArtistDTO> userFollows) {
        this.id = id;
        this.name = name;
        this.userFollows = userFollows;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<ArtistDTO> getUserFollows() {
        return userFollows;
    }

    public void setUserFollows(List<ArtistDTO> userFollows) {
        this.userFollows = userFollows;
    }
}
