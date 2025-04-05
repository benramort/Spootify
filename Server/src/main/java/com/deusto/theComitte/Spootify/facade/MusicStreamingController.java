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
import org.springframework.http.HttpHeaders;

import com.deusto.theComitte.Spootify.service.MusicStreamingService;

@Controller
@RequestMapping("/stream")
public class MusicStreamingController {

    private static final long MAX_CHUNK_SIZE = 2 * 1024 * 1024; // 2MB Cambiar por property
    
    @Autowired
    private MusicStreamingService musicStreamingService;


    @GetMapping()
    public ResponseEntity<Resource> streamMusic(@RequestHeader(value = "Range", required = false) String rangeHeader) {
        long start = 0;
        long end = MAX_CHUNK_SIZE - 1;
        long contentLength = 0;

        Resource fullResource;
        try {
            fullResource = musicStreamingService.getFullResource();
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

        Resource chunk = musicStreamingService.getChunk(start, end);

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
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
