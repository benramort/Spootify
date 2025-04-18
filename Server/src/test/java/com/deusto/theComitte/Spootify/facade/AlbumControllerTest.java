package com.deusto.theComitte.Spootify.facade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.deusto.theComitte.Spootify.DTO.AlbumDTO;
import com.deusto.theComitte.Spootify.DTO.ArtistDTO;
import com.deusto.theComitte.Spootify.entity.Album;
import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.service.AlbumService;
import com.deusto.theComitte.Spootify.service.ArtistService;

public class AlbumControllerTest {

    @Mock
    private AlbumService albumService;
    
    @Mock
    private ArtistService artistService;
    
    @InjectMocks
    private AlbumController albumController;
    
    private final long TOKEN = 12345L;
    private final long ALBUM_ID = 1L;
    private final long ARTIST_ID = 2L;
    private Album testAlbum;
    private Artist testArtist;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create test data
        testArtist = new Artist(ARTIST_ID, "Test Artist", "artist@test.com", "password");
        testAlbum = new Album(ALBUM_ID, "Test Album");
        
        // Set up relationships
        List<Artist> albumArtists = new ArrayList<>();
        albumArtists.add(testArtist);
        testAlbum.setArtists(albumArtists);
        
        List<Album> artistAlbums = new ArrayList<>();
        artistAlbums.add(testAlbum);
        testArtist.setAlbums(artistAlbums);
    }
    
    @Test
    @DisplayName("Create album successfully")
    void testCreateAlbumSuccess() {
        // Arrange
        Album album = new Album(0, "New Album");
        doNothing().when(albumService).createAlbum("New Album", TOKEN);
        
        // Act
        ResponseEntity<Void> response = albumController.createAlbum(album.toDTO(), TOKEN);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(albumService).createAlbum("New Album", TOKEN);
    }
    
    @Test
    @DisplayName("Create album fails when artist not logged in")
    void testCreateAlbumFailsWhenArtistNotLoggedIn() {
        // Arrange
        Album album = new Album(0, "New Album");
        doThrow(new RuntimeException("Artist not logged in")).when(albumService).createAlbum("New Album", TOKEN);
        
        // Act
        ResponseEntity<Void> response = albumController.createAlbum(album.toDTO(), TOKEN);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(albumService).createAlbum("New Album", TOKEN);
    }
    
    @Test
    @DisplayName("Create album fails with general error")
    void testCreateAlbumFailsWithGeneralError() {
        // Arrange
        Album album = new Album(0, "New Album");
        doThrow(new RuntimeException("Other error")).when(albumService).createAlbum("New Album", TOKEN);
        
        // Act
        ResponseEntity<Void> response = albumController.createAlbum(album.toDTO(), TOKEN);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(albumService).createAlbum("New Album", TOKEN);
    }
    
    @Test
    @DisplayName("Get all albums successfully")
    void testGetAllAlbumsSuccess() {
        // Arrange
        List<Album> albums = Arrays.asList(testAlbum);
        when(albumService.getAlbums(0)).thenReturn(albums);
        
        // Act
        ResponseEntity<List<AlbumDTO>> response = albumController.getAlbums(0);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(ALBUM_ID, response.getBody().get(0).getId());
        assertEquals("Test Album", response.getBody().get(0).getName());
        
        // Verify artist data in returned DTO
        assertNotNull(response.getBody().get(0).getArtists());
        assertEquals(1, response.getBody().get(0).getArtists().size());
        assertEquals(ARTIST_ID, response.getBody().get(0).getArtists().get(0).getId());
        assertEquals("Test Artist", response.getBody().get(0).getArtists().get(0).getName());
        
        verify(albumService).getAlbums(0);
    }
    
    @Test
    @DisplayName("Get albums by artist ID successfully")
    void testGetAlbumsByArtistIdSuccess() {
        // Arrange
        List<Album> albums = Arrays.asList(testAlbum);
        when(albumService.getAlbums(ARTIST_ID)).thenReturn(albums);
        
        // Act
        ResponseEntity<List<AlbumDTO>> response = albumController.getAlbums(ARTIST_ID);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(ALBUM_ID, response.getBody().get(0).getId());
        assertEquals("Test Album", response.getBody().get(0).getName());
        
        // Verify artist data in returned DTO
        assertNotNull(response.getBody().get(0).getArtists());
        assertEquals(1, response.getBody().get(0).getArtists().size());
        assertEquals(ARTIST_ID, response.getBody().get(0).getArtists().get(0).getId());
        assertEquals("Test Artist", response.getBody().get(0).getArtists().get(0).getName());
        
        verify(albumService).getAlbums(ARTIST_ID);
    }
    
    @Test
    @DisplayName("Get albums fails with exception")
    void testGetAlbumsFailsWithException() {
        // Arrange
        when(albumService.getAlbums(anyLong())).thenThrow(new RuntimeException("Error getting albums"));
        
        // Act
        ResponseEntity<List<AlbumDTO>> response = albumController.getAlbums(0);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(albumService).getAlbums(0);
    }
    
    @Test
    @DisplayName("Get album by ID successfully")
    void testGetAlbumByIdSuccess() {
        // Arrange
        when(albumService.getAlbum(ALBUM_ID)).thenReturn(testAlbum);
        
        // Act
        ResponseEntity<AlbumDTO> response = albumController.getAlbum(ALBUM_ID);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ALBUM_ID, response.getBody().getId());
        assertEquals("Test Album", response.getBody().getName());
        
        // Verify artist data in returned DTO
        assertNotNull(response.getBody().getArtists());
        assertEquals(1, response.getBody().getArtists().size());
        assertEquals(ARTIST_ID, response.getBody().getArtists().get(0).getId());
        assertEquals("Test Artist", response.getBody().getArtists().get(0).getName());
        
        verify(albumService).getAlbum(ALBUM_ID);
    }
    
    @Test
    @DisplayName("Get album by ID fails when album not found")
    void testGetAlbumByIdFailsWhenAlbumNotFound() {
        // Arrange
        when(albumService.getAlbum(ALBUM_ID)).thenThrow(new RuntimeException("Album not found"));
        
        // Act
        ResponseEntity<AlbumDTO> response = albumController.getAlbum(ALBUM_ID);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(albumService).getAlbum(ALBUM_ID);
    }
    
    @Test
    @DisplayName("Get album by ID fails with general error")
    void testGetAlbumByIdFailsWithGeneralError() {
        // Arrange
        when(albumService.getAlbum(ALBUM_ID)).thenThrow(new RuntimeException("Other error"));
        
        // Act
        ResponseEntity<AlbumDTO> response = albumController.getAlbum(ALBUM_ID);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(albumService).getAlbum(ALBUM_ID);
    }
}