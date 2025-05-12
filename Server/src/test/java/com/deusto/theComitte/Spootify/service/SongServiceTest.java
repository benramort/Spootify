package com.deusto.theComitte.Spootify.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.deusto.theComitte.Spootify.DAO.AlbumRepository;
import com.deusto.theComitte.Spootify.DAO.ArtistRepository;
import com.deusto.theComitte.Spootify.DAO.SongRepository;
import com.deusto.theComitte.Spootify.DAO.UserRepository;
import com.deusto.theComitte.Spootify.entity.Album;
import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.entity.Song;
import com.deusto.theComitte.Spootify.entity.User;

public class SongServiceTest {

    @Mock
    SongRepository songRepository;

    @Mock
    AlbumRepository albumRepository;

    @Mock
    ArtistRepository artistRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ArtistService artistService;

    @InjectMocks
    SongService songService;

    @InjectMocks
    UserService userService;
    
    private Artist testArtist;
    private Album testAlbum;
    private Song testSong;
    private MultipartFile mockFile;
    private final long TOKEN = 12345L;
    private final long ARTIST_ID = 1L;
    private final long ALBUM_ID = 2L;
    private final long SONG_ID = 3L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create test data
        testArtist = new Artist(ARTIST_ID, "Test Artist", "artist@test.com", "password");
        testAlbum = new Album(ALBUM_ID, "Test Album");
        testSong = new Song(SONG_ID, "Test Song", testAlbum, 180, "https://youtube.com/watch?v=test");
        
        // Setup album with artist
        List<Artist> albumArtists = new ArrayList<>();
        albumArtists.add(testArtist);
        testAlbum.setArtists(albumArtists);
        
        // Setup artist with albums
        List<Album> artistAlbums = new ArrayList<>();
        artistAlbums.add(testAlbum);
        testArtist.setAlbums(artistAlbums);
        
        // Setup album with songs
        List<Song> albumSongs = new ArrayList<>();
        albumSongs.add(testSong);
        testAlbum.setSongs(albumSongs);

        mockFile = new MockMultipartFile("name", new byte[0]);
    }
    
    @Test
    @DisplayName("Create song successfully")
    void testCreateSongSuccess() {
        // Arrange
        when(artistService.getActiveArtist(TOKEN)).thenReturn(testArtist);
        when(albumRepository.findById(ALBUM_ID)).thenReturn(testAlbum);
        
        // Act
        try {
            songService.createSong("New Song", 240, mockFile, ALBUM_ID, TOKEN);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
        
        // Assert
        verify(songRepository).save(any(Song.class));
    }
    
    @Test
    @DisplayName("Create song fails when album does not exist for artist")
    void testCreateSongFailsWhenAlbumDoesNotExistForArtist() {
        // Arrange
        Album differentAlbum = new Album(99L, "Different Album");
        when(artistService.getActiveArtist(TOKEN)).thenReturn(testArtist);
        when(albumRepository.findById(99L)).thenReturn(differentAlbum);
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            songService.createSong("New Song", 240, mockFile, 99L, TOKEN)
        );
        verify(songRepository, never()).save(any(Song.class));
    }
    
    @Test
    @DisplayName("Create song fails when artist does not have access to album")
    void testCreateSongFailsWhenArtistDoesNotHaveAccessToAlbum() {
        // Arrange
        Album albumWithDifferentArtist = new Album(ALBUM_ID, "Album Without Access");
        Artist differentArtist = new Artist(99L, "Different Artist", "other@test.com", "pass");
        
        List<Artist> albumArtists = new ArrayList<>();
        albumArtists.add(differentArtist);
        albumWithDifferentArtist.setArtists(albumArtists);
        
        when(artistService.getActiveArtist(TOKEN)).thenReturn(testArtist);
        when(albumRepository.findById(ALBUM_ID)).thenReturn(albumWithDifferentArtist);
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            songService.createSong("New Song", 240, mockFile, ALBUM_ID, TOKEN)
        );
        verify(songRepository, never()).save(any(Song.class));
    }
    
    @Test
    @DisplayName("Get all songs")
    void testGetAllSongs() {
        // Arrange
        List<Song> allSongs = Arrays.asList(testSong, new Song(4L, "Another Song", testAlbum, 200, "url"));
        when(songRepository.findAll()).thenReturn(allSongs);
        
        // Act
        List<Song> result = songService.getSongs(0, 0);
        
        // Assert
        assertEquals(2, result.size());
        verify(songRepository).findAll();
    }
    
    @Test
    @DisplayName("Get songs by artist ID")
    void testGetSongsByArtistId() {
        // Arrange
        List<Song> artistSongs = Arrays.asList(testSong);
        when(songRepository.findByArtistId(ARTIST_ID)).thenReturn(artistSongs);
        
        // Act
        List<Song> result = songService.getSongs(ARTIST_ID, 0);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(testSong, result.get(0));
        verify(songRepository).findByArtistId(ARTIST_ID);
    }
    
    @Test
    @DisplayName("Get songs by album ID")
    void testGetSongsByAlbumId() {
        // Arrange
        List<Song> albumSongs = Arrays.asList(testSong);
        when(songRepository.findByAlbumId(ALBUM_ID)).thenReturn(albumSongs);
        
        // Act
        List<Song> result = songService.getSongs(0, ALBUM_ID);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(testSong, result.get(0));
        verify(songRepository).findByAlbumId(ALBUM_ID);
    }
    
    @Test
    @DisplayName("Get songs by artist ID and album ID")
    void testGetSongsByArtistIdAndAlbumId() {
        // Arrange
        List<Song> filteredSongs = Arrays.asList(testSong);
        when(songRepository.findByArtistIdAndAlbumId(ARTIST_ID, ALBUM_ID)).thenReturn(filteredSongs);
        
        // Act
        List<Song> result = songService.getSongs(ARTIST_ID, ALBUM_ID);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(testSong, result.get(0));
        verify(songRepository).findByArtistIdAndAlbumId(ARTIST_ID, ALBUM_ID);
    }
    
    @Test
    @DisplayName("Get artist songs by token")
    void testGetArtistSongs() {
        // Arrange
        when(artistService.getActiveArtist(TOKEN)).thenReturn(testArtist);
        
        // Act
        List<Song> result = songService.getArtistSongs(TOKEN);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(testSong, result.get(0));
    }
    
    @Test
    @DisplayName("Get artist songs when artist has multiple albums")
    void testGetArtistSongsWithMultipleAlbums() {
        // Arrange
        Album secondAlbum = new Album(5L, "Second Album");
        Song secondSong = new Song(6L, "Second Song", secondAlbum, 210, "url2");
        
        List<Song> secondAlbumSongs = new ArrayList<>();
        secondAlbumSongs.add(secondSong);
        secondAlbum.setSongs(secondAlbumSongs);
        
        testArtist.getAlbums().add(secondAlbum);
        
        when(artistService.getActiveArtist(TOKEN)).thenReturn(testArtist);
        
        // Act
        List<Song> result = songService.getArtistSongs(TOKEN);
        
        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(testSong));
        assertTrue(result.contains(secondSong));
    }
    
    @Test
    @DisplayName("Get song by ID successfully")
    void testGetSongByIdSuccess() {
        // Arrange
        when(songRepository.findById(SONG_ID)).thenReturn(testSong);
        
        // Act
        Song result = songService.getSong(SONG_ID);
        
        // Assert
        assertEquals(testSong, result);
        verify(songRepository).findById(SONG_ID);
    }
    
    @Test
    @DisplayName("Get song by ID throws exception when song does not exist")
    void testGetSongByIdThrowsWhenSongDoesNotExist() {
        // Arrange
        when(songRepository.findById(999L)).thenReturn(null);
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> songService.getSong(999L));
        verify(songRepository).findById(999L);
    }
    
    @Test
    @DisplayName("Create song adds song to album's song list")
    void testCreateSongAddsToAlbumSongList() {
        // Arrange
        Album emptyAlbum = new Album(ALBUM_ID, "Empty Album");
        emptyAlbum.setSongs(new ArrayList<>());
        emptyAlbum.setArtists(Arrays.asList(testArtist));
        
        testArtist.setAlbums(Arrays.asList(emptyAlbum));
        
        when(artistService.getActiveArtist(TOKEN)).thenReturn(testArtist);
        when(albumRepository.findById(ALBUM_ID)).thenReturn(emptyAlbum);
        
        // Act
        try {
            songService.createSong("New Song", 240, mockFile, ALBUM_ID, TOKEN);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
        
        // Assert
        assertEquals(1, emptyAlbum.getSongs().size());
        assertEquals("New Song", emptyAlbum.getSongs().get(0).getName());
        verify(songRepository).save(any(Song.class));
    }

    @Test
    void testSearchSong_Success() {
        String searchTerm = "Test";
        List<Song> expectedSongs = Arrays.asList(testSong);
        when(songRepository.findByName(searchTerm)).thenReturn(expectedSongs);

        List<Song> result = songService.searchSongs(searchTerm);

        assertEquals(expectedSongs, result);
    }

    // @Test
    // @DisplayName("Increment likes for a song successfully")
    // void testDarLikeSuccess() {
    //     // Arrange
    //     when(songRepository.findById(SONG_ID)).thenReturn(testSong);

    //     // Act
    //     songService.darLike(SONG_ID);

    //     // Assert
    //     assertEquals(1, testSong.getNumeroLikes());
    //     verify(songRepository).save(testSong);
    // }

    // @Test
    // @DisplayName("Throw exception when liking a non-existent song")
    // void testDarLikeThrowsWhenSongDoesNotExist() {
    //     // Arrange
    //     when(songRepository.findById(999L)).thenReturn(null);

    //     // Act & Assert
    //     assertThrows(RuntimeException.class, () -> songService.darLike(999L));
    //     verify(songRepository, never()).save(any(Song.class));
    // }

    @Test
    @DisplayName("Get most liked songs successfully")
    void testGetMostLikedSongs() {
        // Arrange
        Song song1 = new Song(1L, "Song 1", testAlbum, 180, "url1");
        song1.setNumeroLikes(5);

        Song song2 = new Song(2L, "Song 2", testAlbum, 200, "url2");
        song2.setNumeroLikes(10);

        Song song3 = new Song(3L, "Song 3", testAlbum, 220, "url3");
        song3.setNumeroLikes(0); // No likes, no debería incluirse

        when(songRepository.findAll()).thenReturn(Arrays.asList(song1, song2, song3));

        // Act
        List<Song> result = songService.getMostLikedSongs();

        // Assert
        assertEquals(2, result.size());
        assertEquals(song2, result.get(0)); // Más likes primero
        assertEquals(song1, result.get(1));
        verify(songRepository).findAll();
    }

    @Test
    @DisplayName("Throw exception when liking a non-existent song")
    void testDarLikeThrowsWhenSongDoesNotExist() {
        // Arrange
        when(songRepository.findById(999L)).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> songService.darLike(999L, TOKEN));
        verify(songRepository, never()).save(any(Song.class));
    }
}