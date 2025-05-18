package com.deusto.theComitte.Spootify.facade;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;

import com.deusto.theComitte.Spootify.service.MusicStreamingService;
/**
 * Esta clase se encarga de gestionar el streaming de música.
 */
@Controller
@RequestMapping("/stream")
public class MusicStreamingController {

    /** Tamaño máximo de chuck */
    private static final long MAX_CHUNK_SIZE = 1024 * 1024; // 2MB Cambiar por property
    
    private MusicStreamingService musicStreamingService;

    @Autowired
    public MusicStreamingController(MusicStreamingService musicStreamingService) {
        this.musicStreamingService = musicStreamingService;
    }

    /**
     * A la función se le puede pedir un rango de bytes de una canción especifiacado en el header Range. 
     * Devuelve el fragmento establecido. Si el rango es mayor al permitido, se devuelve el máximo permitido.
     * Si no se especifica el rango, se devuelve desde el principio hasta el máximo permitido.
     *  @param rangeHeader Rango de bytes a devolver.
     *  @param songId ID de la canción a devolver.
     *  @return Respuesta de HTML con código de "Partial Content" y un Resource con el fragmento de la canción.
     */
    @GetMapping()
    public ResponseEntity<Resource> streamMusic(
        @RequestHeader(value = "Range", required = false) String rangeHeader,
        @RequestParam(name = "song") String songId
    ) {
        long start = 0;
        long end = MAX_CHUNK_SIZE - 1;
        long contentLength = 0;

        String path = "";
        try {
            long songIdlong = Long.parseLong(songId);
            path = musicStreamingService.getSongPath(songIdlong);
            System.out.println(path);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Resource fullResource;
        try {
            fullResource = musicStreamingService.getFullResource(path);
            contentLength = fullResource.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        System.out.println("Header value: "+rangeHeader);
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            rangeHeader = rangeHeader.replace("bytes=", "");
            String[] ranges = rangeHeader.split("-");
            try {
                start = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]);
                } else {
                    // If no end is specified, use the maximum allowed chunk size
                    end = Math.min(start + MAX_CHUNK_SIZE - 1, contentLength - 1);
                }
                end = Math.min(end, start + MAX_CHUNK_SIZE - 1);
            } catch(NumberFormatException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }   
        }

        Resource chunk = musicStreamingService.getChunk(path, start, end);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
            headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
            headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(end - start + 1));
            headers.set(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", start, end, contentLength));

            System.out.println("Returning " + String.format("bytes %d-%d/%d", start, end, contentLength));

            return new ResponseEntity<>(chunk, headers, HttpStatus.PARTIAL_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
