package com.deusto.theComitte.Spootify.DTO;

import java.util.ArrayList;
import java.util.List;

import com.deusto.theComitte.Spootify.entity.Song;

public class SongListDTO {
    private Long id;
    private String name;
    private List<Song> songs = new ArrayList<>();
    private UserDTO userDTO;

    public SongListDTO() {}

    public SongListDTO(String name, UserDTO userDTO) {
        this.name = name;
        this.userDTO = userDTO;
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

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public UserDTO getUser() {
        return userDTO;
    }

    public void setUser(UserDTO userDTO) {
        this.userDTO = userDTO;
    }
}
