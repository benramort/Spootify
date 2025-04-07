package com.deusto.theComitte.Spootify.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.deusto.theComitte.Spootify.DAO.AlbumRepository;
import com.deusto.theComitte.Spootify.DAO.ArtistRepository;
import com.deusto.theComitte.Spootify.entity.Album;
import com.deusto.theComitte.Spootify.entity.Artist;

class AlbumServiceTest {

    @Mock
    private ArtistService artistService;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private AlbumRepository albumRepository;

    @InjectMocks
    private AlbumService albumService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAlbum_Success() {
        long token = 12345L;
        String albumName = "New Album";
    
        // Create an artist with an initialized albums list
        Artist artist = new Artist(1L, "Artist Name", "artist@example.com", "password") {
            private List<Album> albums = new ArrayList<>();
    
            @Override
            public List<Album> getAlbums() {
                return albums;
            }
        };
    
        // Mock the behavior of artistService and albumRepository
        when(artistService.getActiveArtist(token)).thenReturn(artist);
        when(albumRepository.save(any(Album.class))).thenAnswer(invocation -> invocation.getArgument(0));
    
        // Call the method under test
        albumService.createAlbum(albumName, token);
    
        // Assertions
        assertEquals(1, artist.getAlbums().size());
        assertEquals(albumName, artist.getAlbums().get(0).getName());
        verify(albumRepository, times(1)).save(any(Album.class));
        verify(artistRepository, times(1)).save(artist);
    }

@Test
void testGetAlbums_ByArtistId() {
    long artistId = 1L;
    List<Album> albums = new ArrayList<>();
    albums.add(new Album("Album 1"));
    albums.add(new Album("Album 2"));

    // Mock the Artist entity to return the albums list when getAlbums() is called
    Artist artist = mock(Artist.class);
    when(artist.getAlbums()).thenReturn(albums);

    // Mock the repository to return the mocked artist
    when(artistRepository.findById(artistId)).thenReturn(artist);

    // Call the method under test
    List<Album> result = albumService.getAlbums(artistId);

    // Assertions
    assertEquals(2, result.size());
    assertEquals("Album 1", result.get(0).getName());
    assertEquals("Album 2", result.get(1).getName());
}

    @Test
    void testGetAlbums_AllAlbums() {
        List<Album> albums = new ArrayList<>();
        albums.add(new Album("Album 1"));
        albums.add(new Album("Album 2"));

        when(albumRepository.findAll()).thenReturn(albums);

        List<Album> result = albumService.getAlbums(0);

        assertEquals(2, result.size());
        assertEquals("Album 1", result.get(0).getName());
        assertEquals("Album 2", result.get(1).getName());
    }

    @Test
    void testGetAlbum_Success() {
        long albumId = 1L;
        Album album = new Album("Album Name");

        when(albumRepository.findById(albumId)).thenReturn(album);

        Album result = albumService.getAlbum(albumId);

        assertEquals(album, result);
    }

    @Test
    void testGetAlbum_NotFound() {
        long albumId = 1L;

        when(albumRepository.findById(albumId)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            albumService.getAlbum(albumId);
        });

        assertEquals("Album not found", exception.getMessage());
    }



}