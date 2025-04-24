package com.deusto.theComitte.Spootify.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.deusto.theComitte.Spootify.DAO.SongRepository;
import com.deusto.theComitte.Spootify.entity.Song;

@Service
public class MusicStreamingService {

    @Autowired
    SongRepository songRepository;

    public Resource getChunk(String path, long start, long end) {

        // Song song = songRepository.findById(songId);
        // if (song == null) {
        //     throw new RuntimeException("Song not found");
        // }

        try {
            InputStream inputStream = new FileInputStream(path);
            inputStream.skip(start);
            InputStreamResource resource = new InputStreamResource(new LimitedInputStream(inputStream, end));
            return resource;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }

    public Resource getFullResource(String path) throws IOException {
        InputStream inputStream = new FileInputStream(path);
        InputStreamResource resource = new InputStreamResource(inputStream);
        return resource;
    }

    public String getSongPath(long songId) {
        Song song = songRepository.findById(songId);
        if (song == null) {
            throw new RuntimeException("Song not found");
        }
        return song.getSongPath();
    }
    
}
