package com.deusto.theComitte.Spootify.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class MusicStreamingService {

    public Resource getChunk(long start, long end) {
        try {
            InputStream inputStream = new FileInputStream("prueba.mp3");
            inputStream.skip(start);
            InputStreamResource resource = new InputStreamResource(new LimitedInputStream(inputStream, end));
            return resource;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }

    public Resource getFullResource() throws IOException {
        InputStream inputStream = new FileInputStream("prueba.mp3");
        InputStreamResource resource = new InputStreamResource(inputStream);
        return resource;
    }
    
}
