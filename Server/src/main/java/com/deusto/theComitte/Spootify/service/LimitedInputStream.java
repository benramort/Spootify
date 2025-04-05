package com.deusto.theComitte.Spootify.service;

import java.io.IOException;
import java.io.InputStream;

public class LimitedInputStream extends InputStream {  //Esta clase permite leer de un inputStream hasta cierto punto

    private final InputStream inputStream;
    private long bytesRemaining;

    public LimitedInputStream(InputStream inputStream, long maxBytes) {
        this.inputStream = inputStream;
        this.bytesRemaining = maxBytes;
    }

    @Override
    public int read() throws IOException {
        if (bytesRemaining <= 0) {
            return -1; // End of stream
        }
        int byteRead = inputStream.read();
        if (byteRead != -1) {
            bytesRemaining--;
        }
        return byteRead;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (bytesRemaining <= 0) {
            return -1; // End of stream
        }
        int bytesToRead = (int) Math.min(len, bytesRemaining);
        int bytesRead = inputStream.read(b, off, bytesToRead);
        if (bytesRead != -1) {
            bytesRemaining -= bytesRead;
        }
        return bytesRead;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    

}
