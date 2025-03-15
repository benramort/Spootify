package com.deusto.theComitte.Spootify.entity;

public class Song {
    
    private long id;
    private String name;
    private Artist artist;
    // private Album album;
    private int duration;
    private String youtubeUrl;

    public Song(long id, String name, Artist artist, int duration, String youtubeUrl) {
        this.id = id;
        this.artist = artist;
        this.name = name;
        this.duration = duration;
        this.youtubeUrl = youtubeUrl;
    }

    public Song() {

    }

    public long getId() {
        return id;
    }

    public Artist getArtist() {
        return artist;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public void setId(long id) {
        this.id = id;
    }
}
