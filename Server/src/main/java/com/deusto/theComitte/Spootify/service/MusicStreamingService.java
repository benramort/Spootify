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

    SongRepository songRepository;

    @Autowired
    public MusicStreamingService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    /**
     * Devuelve un fragmento del archivo especificado en el path.
     * @param path Ruta de la canción
     * @param start Inicio del fragmento
     * @param end Fin del fragmento
     * @return Resource con el fragmento de la canción
     * @throws IOException si ocurre un error al leer el archivo
     */
    public Resource getChunk(String path, long start, long end) {

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


    /**
     * Devuelve el recurso completo del archivo especificado en el path.
     * @param path Ruta de la canción
     * @return Resource con el recurso completo de la canción
     * @throws IOException si ocurre un error al leer el archivo
     */
    public Resource getFullResource(String path) throws IOException {
        InputStream inputStream = new FileInputStream(path);
        InputStreamResource resource = new InputStreamResource(inputStream);
        return resource;
    }

    /**
     * Devuelve la ruta de la canción especificada por su ID.
     * @param songId ID de la canción
     * @return Ruta de la canción
     */
    public String getSongPath(long songId) {
        Song song = songRepository.findById(songId);
        if (song == null) {
            throw new RuntimeException("Song not found");
        }
        return song.getSongPath();
    }
    
}
