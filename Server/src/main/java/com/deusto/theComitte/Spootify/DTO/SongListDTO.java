package com.deusto.theComitte.Spootify.DTO;

import java.util.ArrayList;
import java.util.List;

public class SongListDTO {
    private Long id;
    private String name;
    private boolean isPublic;
    private String shareLink;
    private List<SongDTO> songs;

    public SongListDTO() {}

    public SongListDTO(String name, boolean isPublic) {
        this.name = name;
        this.isPublic = isPublic;
        this.songs = new ArrayList<>();
    }

    public SongListDTO(Long id, boolean isPublic, String name) {
        this.id = id;
        this.name = name;
        this.isPublic = isPublic;
        this.songs = new ArrayList<>();
    }

    public SongListDTO(Long id, String name, boolean isPublic, List<SongDTO> songs) {
        this.id = id;
        this.name = name;
        this.isPublic = isPublic;
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

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public boolean getIsPublic() {
        return this.isPublic;
    }

    public String getShareLink() {
        return shareLink;
    }

    public List<SongDTO> getSongs() {
        return songs;
    }

    public void setSongs(List<SongDTO> songs) {
        this.songs = songs;
    }
}
