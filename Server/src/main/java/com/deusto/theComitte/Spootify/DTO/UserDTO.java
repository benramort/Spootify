package com.deusto.theComitte.Spootify.DTO;

import java.util.List;


public class UserDTO {
    private long id;
    private String name;
    private List<UserDTO> userFriends;
    private List<SongDTO> userSongs;

    public UserDTO() {
    }

    public UserDTO(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserDTO(long id, String name, List<UserDTO> userFriends) {
        this.id = id;
        this.name = name;
        this.userFriends = userFriends;
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

    public void setFriends(List<UserDTO> userFriends) {
        this.userFriends = userFriends;
    }

    public List<UserDTO> getUserFriends() {
        return this.userFriends;
    }

    public void setSongs(List<SongDTO> userSongs) {
        this.userSongs = userSongs;
    }

    public List<SongDTO> getUserSongs() {
        return this.userSongs;
    }
}
