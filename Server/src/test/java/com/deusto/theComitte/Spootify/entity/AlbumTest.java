package com.deusto.theComitte.Spootify.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AlbumTest {

    @Test
    @DisplayName("Constructor")
    void testAlbum() {
        List<Song> songs = new ArrayList<>();
        List<Artist> artists = new ArrayList<>();
        Album album = new Album(1, "Album", artists, songs);
        assertEquals(1, album.getId());
        assertEquals("Album", album.getName());
        assertEquals(artists, album.getArtists());
        assertEquals(songs, album.getSongs());
        // assertEquals(1, 2);
    }
    
}
