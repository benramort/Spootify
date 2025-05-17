package com.deusto.theComitte.Spootify.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;

import com.deusto.theComitte.Spootify.DAO.SongRepository;
import com.deusto.theComitte.Spootify.entity.Album;
import com.deusto.theComitte.Spootify.entity.Song;

class MusicStreamingServiceTest {

    @Mock
    private SongRepository songRepository; 


    @InjectMocks
    private MusicStreamingService musicStreamingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetChunk_Success() throws Exception {
        // Arrange
        String content = "This is a test song for streaming.";
        File tempFile = File.createTempFile("test-song", ".txt");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(content.getBytes());
        }

        String path = tempFile.getAbsolutePath();
        long start = 5; // Start reading from the 5th byte
        long end = 15; // Read up to the 15th byte

        // Act
        Resource result = musicStreamingService.getChunk(path, start, end);

        // Assert
        assertNotNull(result);
        byte[] buffer = new byte[10];
        int bytesRead = result.getInputStream().read(buffer);
        assertEquals(10, bytesRead);
        assertEquals("is a test ", new String(buffer));

        // Clean up
        Files.deleteIfExists(tempFile.toPath());
    }


    @Test
    void testGetFullResource_Success() throws Exception {
        // Arrange
        String content = "This is a full resource test.";
        File tempFile = File.createTempFile("test-full-resource", ".txt");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(content.getBytes());
        }

        String path = tempFile.getAbsolutePath();

        // Act
        Resource result = musicStreamingService.getFullResource(path);

        // Assert
        assertNotNull(result);
        byte[] buffer = new byte[content.length()];
        int bytesRead = result.getInputStream().read(buffer);
        assertEquals(content.length(), bytesRead);
        assertEquals(content, new String(buffer));

        // Clean up
        Files.deleteIfExists(tempFile.toPath());
    }

    @Test
void testGetSongPath_Success() {
    // Arrange
    long songId = 1L;
    Album album = new Album("Test Album");
    Song song = new Song("TestSong",album,180, "Test Artist");
    song.setSongPath("test/path/to/song.mp3");

    when(songRepository.findById(songId)).thenReturn(song);

    // Act
    String result = musicStreamingService.getSongPath(songId);

    // Assert
    assertNotNull(result);
    assertEquals("test/path/to/song.mp3", result);
    verify(songRepository).findById(songId);
}

@Test
void testGetSongPath_SongNotFound() {
    // Arrange
    long songId = 1L;

    when(songRepository.findById(songId)).thenReturn(null);

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        musicStreamingService.getSongPath(songId);
    });
    assertEquals("Song not found", exception.getMessage());
    verify(songRepository).findById(songId);
}
}