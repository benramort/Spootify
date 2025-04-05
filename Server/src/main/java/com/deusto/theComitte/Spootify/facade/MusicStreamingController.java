package com.deusto.theComitte.Spootify.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.deusto.theComitte.Spootify.service.MusicStreamingService;

@Controller
@RequestMapping("/stream")
public class MusicStreamingController {

    private static final long MAX_CHUNK_SIZE = 1024 * 1024; // 1MB Cambiar por property
    
    @Autowired
    private MusicStreamingService musicStreamingService;


    @GetMapping()
    public ResponseEntity<Resource> streamMusic(@RequestHeader(value = "Range", required = false) String rangeHeader) {
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            rangeHeader.replace("bytes=", "");
            String[] ranges = rangeHeader.split("-");
            try {
                long start = Long.parseLong(ranges[0]);
                long end = Long.parseLong(ranges[1]);
                end = Math.min(end, start + MAX_CHUNK_SIZE - 1);
                musicStreamingService.getChunk(start, end);
            } catch(NumberFormatException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }   
        }
        if (rangeHeader == null) {
            musicStreamingService.getChunk(0, MAX_CHUNK_SIZE);
        }


        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
