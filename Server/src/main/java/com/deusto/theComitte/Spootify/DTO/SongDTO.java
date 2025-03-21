package com.deusto.theComitte.Spootify.DTO;

public class SongDTO {
    private long id;
    private String title;
    private AlbumDTO album;
    private int duration; // duration in seconds
    private String youtubeUrl;

    public SongDTO() {
    }

    public SongDTO(long id, String title, AlbumDTO album, int duration, String youtubeUrl) {
        this.id = id;
        this.title = title;
        this.album = album;
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

    public AlbumDTO getAlbum() {
        return album;
    }

    public void setAlbum(AlbumDTO album) {
        this.album = album;
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
