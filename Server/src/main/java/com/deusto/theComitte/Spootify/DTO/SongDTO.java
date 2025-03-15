package com.deusto.theComitte.Spootify.DTO;

public class SongDTO {
    private long id;
    private String title;
    private ArtistDTO artist;
    private int duration; // duration in seconds
    private String youtubeUrl;

    public SongDTO() {
    }

    public SongDTO(long id, String title, ArtistDTO artist, int duration, String youtubeUrl) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.youtubeUrl = youtubeUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArtistDTO getArtist() {
        return artist;
    }

    public void setArtist(ArtistDTO artist) {
        this.artist = artist;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }
}
