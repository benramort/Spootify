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

import com.deusto.theComitte.Spootify.DAO.ArtistRepository;
import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.entity.User;

class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistService artistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateArtist_Success() {
        String name = "Artist Name";
        String email = "artist@example.com";
        String password = "password";

        when(artistRepository.findByEmail(email)).thenReturn(null);

        artistService.createArtist(name, email, password);

        verify(artistRepository, times(1)).save(any(Artist.class));
    }

    @Test
    void testCreateArtist_AlreadyExists() {
        String email = "artist@example.com";

        when(artistRepository.findByEmail(email)).thenReturn(new Artist());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            artistService.createArtist("Artist Name", email, "password");
        });

        assertEquals("Artist already exists", exception.getMessage());
        verify(artistRepository, never()).save(any(Artist.class));
    }

    @Test
    void testLogin_Success() {
        String email = "artist@example.com";
        String password = "password";
        Artist artist = new Artist(1L, "Artist Name", email, password);

        when(artistRepository.findByEmail(email)).thenReturn(artist);

        try {
            long token = artistService.login(email, password);
            assertNotEquals(0, token);
        } catch (RuntimeException e) {
            fail("Login should not throw an exception");
        }
        

        
    }

    @Test
    void testLogin_ArtistDoesNotExist() {
        String email = "artist@example.com";

        when(artistRepository.findByEmail(email)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            artistService.login(email, "password");
        });

        assertEquals("Artist does not exist", exception.getMessage());
    }

    @Test
    void testLogin_IncorrectPassword() {
        String email = "artist@example.com";
        String password = "wrongPassword";
        Artist artist = new Artist(1L, "Artist Name", email, "password");

        when(artistRepository.findByEmail(email)).thenReturn(artist);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            artistService.login(email, password);
        });

        assertEquals("Incorrect password", exception.getMessage());
    }

    @Test
    void testLogout_Success() {
        long token = 12345L;
        Artist artist = new Artist(1L, "Artist Name", "artist@example.com", "password");

        artistService.getActiveArtistMap().put(token, artist);

        artistService.logout(token);

        assertFalse(artistService.getActiveArtistMap().containsKey(token));
    }

    @Test
    void testLogout_ArtistNotLoggedIn() {
        long token = 12345L;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            artistService.logout(token);
        });

        assertEquals("Artist not logged in", exception.getMessage());
    }

    @Test
    void testGetArtists() {
        List<Artist> artists = new ArrayList<>();
        artists.add(new Artist(1L, "Artist 1", "artist1@example.com", "password1"));
        artists.add(new Artist(2L, "Artist 2", "artist2@example.com", "password2"));

        when(artistRepository.findAll()).thenReturn(artists);

        List<Artist> result = artistService.getArtists();

        assertEquals(2, result.size());
        assertEquals("Artist 1", result.get(0).getName());
        assertEquals("Artist 2", result.get(1).getName());
    }

    @Test
    void testGetArtist_Success() {
        long id = 1L;
        Artist artist = new Artist(id, "Artist Name", "artist@example.com", "password");

        when(artistRepository.findById(id)).thenReturn(artist);

        Artist result = artistService.getArtist(id);

        assertEquals(artist, result);
    }

    @Test
    void testGetArtist_ArtistDoesNotExist() {
        long id = 1L;

        when(artistRepository.findById(id)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            artistService.getArtist(id);
        });

        assertEquals("Artist does not exist", exception.getMessage());
    }

    @Test
    void testGetActiveArtist_Success() {
        long token = 12345L;
        Artist artist = new Artist(1L, "Artist Name", "artist@example.com", "password");

        artistService.getActiveArtistMap().put(token, artist);

        Artist result = artistService.getActiveArtist(token);

        assertEquals(artist, result);
    }

    @Test
    void testGetActiveArtist_ArtistNotLoggedIn() {
        long token = 12345L;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            artistService.getActiveArtist(token);
        });

        assertEquals("Artist not logged in", exception.getMessage());
    }

    @Test
    void testSearchArtist_Success() {
        String name = "Artist Name";
        List<Artist> artists = new ArrayList<>();
        artists.add(new Artist(1L, "Artist Name", "artist@example.com", "password"));

        when(artistRepository.findByName(name)).thenReturn(artists);

        // Act
        List<Artist> result = artistService.searchArtists(name);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Artist Name", result.get(0).getName());
        assertEquals("artist@example.com", result.get(0).getEmail());
        verify(artistRepository).findByName(name);
    }
    
    @Test
    void testGetMostFollowedArtists() {
        // Arrange
        Artist artist1 = new Artist(1L, "Artist 1", "artist1@email.com", "pass1");
        Artist artist2 = new Artist(2L, "Artist 2", "artist2@email.com", "pass2");
        Artist artist3 = new Artist(3L, "Artist 3", "artist3@email.com", "pass3");

        // Simular seguidores
        artist1.getFollowersList().add(new User()); // 1 seguidor
        artist2.getFollowersList().add(new User());
        artist2.getFollowersList().add(new User()); // 2 seguidores
        // artist3 sin seguidores

        List<Artist> artists = List.of(artist1, artist2, artist3);

        when(artistRepository.findAll()).thenReturn(artists);

        // Act
        List<Artist> result = artistService.getMostFollowedArtists();

        // Assert
        assertEquals(3, result.size());
        assertEquals(artist2, result.get(0)); // Más seguidores primero
        assertEquals(artist1, result.get(1));
        assertEquals(artist3, result.get(2));
        verify(artistRepository).findAll();
    }
}