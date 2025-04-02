package com.deusto.theComitte.Spootify.DTO;

import java.util.List;


public class UserDTO {
    private long id;
    private String name;
<<<<<<< HEAD
    private List<UserDTO> userFriends;
    private List<SongDTO> userSongs;
=======
    private List<ArtistDTO> userFollows;
    private List<SongListDTO> playlists;
>>>>>>> main

    public UserDTO() {
    }

    public UserDTO(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserDTO(long id, String name,  List<ArtistDTO> userFollows, List<SongListDTO> playlists) {
        this.id = id;
        this.name = name;
        this.userFollows = userFollows;
        this.playlists = playlists;
    }

    public UserDTO(long id, String name, List<UserDTO> userFriends, List<SongDTO> userSongs) {
        this.id = id;
        this.name = name;
        this.userFriends = userFriends;
        this.userSongs = userSongs;
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

    public List<SongListDTO> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<SongListDTO> playlists) {
        this.playlists = playlists;
    }

    public void setSongs(List<SongDTO> userSongs) {
        this.userSongs = userSongs;
    }

    public List<SongDTO> getUserSongs() {
        return this.userSongs;
    }
}
