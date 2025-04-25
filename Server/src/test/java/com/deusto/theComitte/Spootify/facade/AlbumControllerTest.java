package com.deusto.theComitte.Spootify.facade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

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
    private MockMultipartFile coverFile;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create test data
        testArtist = new Artist(ARTIST_ID, "Test Artist", "artist@test.com", "password");
        testAlbum = new Album(ALBUM_ID, "Test Album");
        testAlbum.setCover("imagenes/test_cover.jpg");
        
        // Set up relationships
        List<Artist> albumArtists = new ArrayList<>();
        albumArtists.add(testArtist);
        testAlbum.setArtists(albumArtists);
        
        List<Album> artistAlbums = new ArrayList<>();
        artistAlbums.add(testAlbum);
        testArtist.setAlbums(artistAlbums);
        
        // Create a mock cover file
        coverFile = new MockMultipartFile("cover", "test_cover.jpg", "image/jpeg", "test image content".getBytes());
    }
    
    @Test
    @DisplayName("Create album successfully")
    void testCreateAlbumSuccess() {
        // Arrange
        String albumName = "New Album";
        doNothing().when(albumService).createAlbum(eq(albumName), isNull(), eq(TOKEN));
        
        // Act
        ResponseEntity<Void> response = albumController.createAlbum(albumName, null, TOKEN);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(albumService).createAlbum(albumName, null, TOKEN);
    }
    
    @Test
    @DisplayName("Create album with cover successfully")
    void testCreateAlbumWithCoverSuccess() throws Exception {
        // Arrange
        String albumName = "New Album";
        
        // We're using any(String.class) for cover path since it's generated with timestamp
        doNothing().when(albumService).createAlbum(eq(albumName), any(String.class), eq(TOKEN));
        
        // Act
        ResponseEntity<Void> response = albumController.createAlbum(albumName, coverFile, TOKEN);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(albumService).createAlbum(eq(albumName), any(String.class), eq(TOKEN));
    }
    
    @Test
    @DisplayName("Create album fails when artist not logged in")
    void testCreateAlbumFailsWhenArtistNotLoggedIn() {
        // Arrange
        String albumName = "New Album";
        doThrow(new RuntimeException("Artist not logged in"))
            .when(albumService).createAlbum(eq(albumName), isNull(), eq(TOKEN));
        
        // Act
        ResponseEntity<Void> response = albumController.createAlbum(albumName, null, TOKEN);
        
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(albumService).createAlbum(albumName, null, TOKEN);
    }
    
    @Test
    @DisplayName("Create album fails with general error")
    void testCreateAlbumFailsWithGeneralError() {
        // Arrange
        String albumName = "New Album";
        doThrow(new RuntimeException("Other error"))
            .when(albumService).createAlbum(eq(albumName), isNull(), eq(TOKEN));
        
        // Act
        ResponseEntity<Void> response = albumController.createAlbum(albumName, null, TOKEN);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(albumService).createAlbum(albumName, null, TOKEN);
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
        assertEquals("imagenes/test_cover.jpg", response.getBody().get(0).getCover());
        
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
        assertEquals("imagenes/test_cover.jpg", response.getBody().get(0).getCover());
        
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
        assertEquals("http://localhost:8081/"+"imagenes/test_cover.jpg", response.getBody().getCover());
        
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
    
    @Test
    @DisplayName("Search albums by name successfully")
    void testSearchAlbumsByNameSuccess() {
        // Arrange
        String searchName = "Test";
        List<Album> albums = Arrays.asList(testAlbum);
        when(albumService.searchAlbums(searchName)).thenReturn(albums);
        
        // Act
        ResponseEntity<List<AlbumDTO>> response = albumController.searchAlbums(searchName);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(ALBUM_ID, response.getBody().get(0).getId());
        assertEquals("Test Album", response.getBody().get(0).getName());
        
        verify(albumService).searchAlbums(searchName);
    }
    
    @Test
    @DisplayName("Search albums fails when no albums found")
    void testSearchAlbumsFailsWhenNoAlbumsFound() {
        // Arrange
        String searchName = "Nonexistent";
        when(albumService.searchAlbums(searchName))
            .thenThrow(new RuntimeException("No albums found with the given name"));
        
        // Act
        ResponseEntity<List<AlbumDTO>> response = albumController.searchAlbums(searchName);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(albumService).searchAlbums(searchName);
    }
    
    @Test
    @DisplayName("Search albums fails with general error")
    void testSearchAlbumsFailsWithGeneralError() {
        // Arrange
        String searchName = "Error";
        when(albumService.searchAlbums(searchName))
            .thenThrow(new RuntimeException("Other error"));
        
        // Act
        ResponseEntity<List<AlbumDTO>> response = albumController.searchAlbums(searchName);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(albumService).searchAlbums(searchName);
    }

    @Test
    @DisplayName("find by name successfully")
    void testFindByNameSuccess() {
        String searchName = "Jamon y queso";
        Album album = new Album(ALBUM_ID, "Jamon y queso");
        List<Album> albums = Arrays.asList(album);
        when(albumService.searchAlbums(searchName)).thenReturn(albums);

        ResponseEntity<List<AlbumDTO>> response = albumController.searchAlbums(searchName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(ALBUM_ID, response.getBody().get(0).getId());
        assertEquals("Jamon y queso", response.getBody().get(0).getName());

        verify(albumService).searchAlbums(searchName);
    }

}