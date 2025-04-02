package com.deusto.theComitte.Spootify.DTO;

import java.util.ArrayList;
import java.util.List;

public class SongListDTO {
    private Long id;
    private String name;
    private List<SongDTO> songs;

    public SongListDTO() {}

    public SongListDTO(String name) {
        this.name = name;
        this.songs = new ArrayList<>();
    }

    public SongListDTO(Long id, String name) {
        this.id = id;
        this.name = name;
        this.songs = new ArrayList<>();
    }

    public SongListDTO(Long id, String name, List<SongDTO> songs) {
        this.id = id;
        this.name = name;
        this.songs = songs;
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

    public List<SongDTO> getSongs() {
        return songs;
    }

    public void setSongs(List<SongDTO> songs) {
        this.songs = songs;
    }
}
