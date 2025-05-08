package com.deusto.theComitte.Spootify.facade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.deusto.theComitte.Spootify.DTO.AlbumDTO;
import com.deusto.theComitte.Spootify.DTO.SongDTO;
import com.deusto.theComitte.Spootify.entity.Album;
import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.entity.Song;
import com.deusto.theComitte.Spootify.service.SongService;

public class SongControllerTest {

    @Mock
    private SongService songService;

    @InjectMocks
    private SongController songController;

    private final long ARTIST_ID = 1L;
    private final long ALBUM_ID = 1L;
    private final long TOKEN = 12345L;
    private final String SONG_TITLE = "Test Song";
    private final int DURATION = 300;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Get songs successfully")
    void testGetSongsSuccess() {
        // Arrange
        List<Song> songs = new ArrayList<>();
        Song song = new Song();
        song.setName(SONG_TITLE);

        // Simular que la canción pertenece a un álbum y un artista
        Album album = new Album();
        album.setId(ALBUM_ID);
        Artist artist = new Artist();
        artist.setId(ARTIST_ID);
        List<Artist> artists = new ArrayList<>();
        artists.add(artist);
        album.setArtists(artists);
        song.setAlbum(album);

        songs.add(song);

        when(songService.getSongs(ARTIST_ID, ALBUM_ID)).thenReturn(songs);

        // Act
        ResponseEntity<List<SongDTO>> response = songController.getSongs(ARTIST_ID, ALBUM_ID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(SONG_TITLE, response.getBody().get(0).getTitle());
        verify(songService).getSongs(ARTIST_ID, ALBUM_ID);
    }

    @Test
    @DisplayName("Get songs fails with general error")
    void testGetSongsFailsWithGeneralError() {
        // Arrange
        doThrow(new RuntimeException("General error"))
            .when(songService).getSongs(ARTIST_ID, ALBUM_ID);

        // Act
        ResponseEntity<List<SongDTO>> response = songController.getSongs(ARTIST_ID, ALBUM_ID);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(songService).getSongs(ARTIST_ID, ALBUM_ID);
    }

    @Test
    @DisplayName("Create song successfully")
    void testCreateSongSuccess() throws IOException {
        // Arrange
        MultipartFile audioFile = mock(MultipartFile.class);

        doNothing().when(songService).createSong(SONG_TITLE, DURATION, audioFile, ALBUM_ID, TOKEN);

        // Act
        ResponseEntity<Void> response = songController.createSong(SONG_TITLE, ALBUM_ID, DURATION, audioFile, TOKEN);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(songService).createSong(SONG_TITLE, DURATION, audioFile, ALBUM_ID, TOKEN);
    }

    @Test
    @DisplayName("Create song fails when artist not logged in")
    void testCreateSongFailsWhenArtistNotLoggedIn() throws IOException {
        // Arrange
        MultipartFile audioFile = mock(MultipartFile.class);

        doThrow(new RuntimeException("Artist not logged in"))
            .when(songService).createSong(SONG_TITLE, DURATION, audioFile, ALBUM_ID, TOKEN);

        // Act
        ResponseEntity<Void> response = songController.createSong(SONG_TITLE, ALBUM_ID, DURATION, audioFile, TOKEN);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(songService).createSong(SONG_TITLE, DURATION, audioFile, ALBUM_ID, TOKEN);
    }

    @Test
    @DisplayName("Create song fails when album does not exist")
    void testCreateSongFailsWhenAlbumDoesNotExist() throws IOException {
        // Arrange
        MultipartFile audioFile = mock(MultipartFile.class);

        doThrow(new RuntimeException("Album does not exist"))
            .when(songService).createSong(SONG_TITLE, DURATION, audioFile, ALBUM_ID, TOKEN);

        // Act
        ResponseEntity<Void> response = songController.createSong(SONG_TITLE, ALBUM_ID, DURATION, audioFile, TOKEN);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(songService).createSong(SONG_TITLE, DURATION, audioFile, ALBUM_ID, TOKEN);
    }

    @Test
    @DisplayName("Search songs successfully")
    void testSearchSongsSuccess() {
        // Arrange
        List<Song> songs = new ArrayList<>();
        Song song = new Song();
        song.setName(SONG_TITLE);

        // Simular que la canción pertenece a un álbum y un artista
        Album album = new Album();
        album.setId(ALBUM_ID);
        Artist artist = new Artist();
        artist.setId(ARTIST_ID);
        List<Artist> artists = new ArrayList<>();
        artists.add(artist);
        album.setArtists(artists);
        song.setAlbum(album);

        songs.add(song);

        when(songService.searchSongs(SONG_TITLE)).thenReturn(songs);

        // Act
        ResponseEntity<List<SongDTO>> response = songController.searchSongs(SONG_TITLE);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(SONG_TITLE, response.getBody().get(0).getTitle());
        verify(songService).searchSongs(SONG_TITLE);
    }

    @Test
    @DisplayName("Search songs fails with general error")
    void testSearchSongsFailsWithGeneralError() {
        // Arrange
        doThrow(new RuntimeException("General error"))
            .when(songService).searchSongs(SONG_TITLE);

        // Act
        ResponseEntity<List<SongDTO>> response = songController.searchSongs(SONG_TITLE);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(songService).searchSongs(SONG_TITLE);
    }

    @Test
    @DisplayName("Create song fails with IOException")
    void testCreateSongFailsWithIOException() throws IOException {

        // Configurar el mock para lanzar IOException al intentar acceder al archivo
        doThrow(new IOException("Error reading file")).when(songService).createSong(SONG_TITLE, DURATION, null, ALBUM_ID, TOKEN);

        // Act
        ResponseEntity<Void> response = songController.createSong(SONG_TITLE, ALBUM_ID, DURATION, null, TOKEN);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(songService, never()).createSong(anyString(), anyInt(), any(MultipartFile.class), anyLong(), anyLong());
    }

    // @Test
    // @DisplayName("Like a song successfully")
    // void testLikeSongSuccess() {
    //     // Arrange
    //     long songId = 1L;

    //     doNothing().when(songService).darLike(songId);

    //     // Act
    //     ResponseEntity<Void> response = songController.likeSong(songId);

    //     // Assert
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     verify(songService).darLike(songId);
    // }

    // @Test
    // @DisplayName("Like a song fails when song does not exist")
    // void testLikeSongFailsWhenSongDoesNotExist() {
    //     // Arrange
    //     long songId = 999L;

    //     doThrow(new RuntimeException("Song does not exist")).when(songService).darLike(songId);

    //     // Act
    //     ResponseEntity<Void> response = songController.likeSong(songId);

    //     // Assert
    //     assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    //     verify(songService).darLike(songId);
    // }

    // @Test
    // @DisplayName("Like a song fails when user not logged in")
    // void testLikeSongFailsWhenUserNotLoggedIn() {
    //     // Arrange
    //     long songId = 1L;

    //     doThrow(new RuntimeException("User not logged in")).when(songService).darLike(songId);

    //     // Act
    //     ResponseEntity<Void> response = songController.likeSong(songId);

    //     // Assert
    //     assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    //     verify(songService).darLike(songId);
    // }

    // @Test
    // @DisplayName("Get most liked songs successfully")
    // void testGetMostLikedSongsSuccess() {
    //     // Arrange
    //     Album album = new Album();
    //     album.setName("Album 1");

    //     Song song1 = new Song();
    //     song1.setName("Song 1");
    //     song1.setNumeroLikes(10);
    //     song1.setAlbum(album); // Asignar un álbum válido

    //     Song song2 = new Song();
    //     song2.setName("Song 2");
    //     song2.setNumeroLikes(5);
    //     song2.setAlbum(album); // Asignar un álbum válido

    //     List<Song> songs = new ArrayList<>();
    //     songs.add(song1);
    //     songs.add(song2);

    //     when(songService.getMostLikedSongs()).thenReturn(songs);

    //     // Act
    //     ResponseEntity<List<SongDTO>> response = songController.getMostLikedSongs();

    //     // Assert
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     assertNotNull(response.getBody());
    //     assertEquals(2, response.getBody().size());
    //     assertEquals("Song 1", response.getBody().get(0).getTitle());
    //     assertEquals("Song 2", response.getBody().get(1).getTitle());
    //     verify(songService).getMostLikedSongs();
    // }

    @Test
    @DisplayName("Get most liked songs successfully")
    void testGetMostLikedSongsSuccess() {
        // Arrange
        Album album = mock(Album.class);
        when(album.toDTOWithoutSongs()).thenReturn(new AlbumDTO(1L, "Album 1", null));

        Song song1 = new Song(1L, "Song 1", album, 180, "path/to/song1");
        song1.setNumeroLikes(10);

        Song song2 = new Song(2L, "Song 2", album, 200, "path/to/song2");
        song2.setNumeroLikes(5);

        List<Song> songs = new ArrayList<>();
        songs.add(song1);
        songs.add(song2);

        when(songService.getMostLikedSongs()).thenReturn(songs);

        // Act
        ResponseEntity<List<SongDTO>> response = songController.getMostLikedSongs();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        SongDTO songDTO1 = response.getBody().get(0);
        assertEquals("Song 1", songDTO1.getTitle());
        assertEquals(10, songDTO1.getNumeroLikes());
        assertEquals(180, songDTO1.getDuration());
        assertNotNull(songDTO1.getAlbum());

        SongDTO songDTO2 = response.getBody().get(1);
        assertEquals("Song 2", songDTO2.getTitle());
        assertEquals(5, songDTO2.getNumeroLikes());
        assertEquals(200, songDTO2.getDuration());
        assertNotNull(songDTO2.getAlbum());

        verify(songService).getMostLikedSongs();
    }

    @Test
    @DisplayName("Get most liked songs fails with general error")
    void testGetMostLikedSongsFailsWithGeneralError() {
        // Arrange
        doThrow(new RuntimeException("General error")).when(songService).getMostLikedSongs();

        // Act
        ResponseEntity<List<SongDTO>> response = songController.getMostLikedSongs();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(songService).getMostLikedSongs();
    }
}