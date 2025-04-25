package com.deusto.theComitte.Spootify.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LimitedInputStreamTest {

    private byte[] testData;

    @BeforeEach
    void setUp() {
        // Create test data with 20 bytes
        testData = "This is a test string".getBytes(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("read() should read single bytes until limit is reached")
    void testReadSingleBytesWithLimit() throws IOException {
        // Arrange
        InputStream baseStream = new ByteArrayInputStream(testData);
        long limit = 5; // Limit to first 5 bytes
        LimitedInputStream limitedStream = new LimitedInputStream(baseStream, limit);
        
        // Act & Assert
        for (int i = 0; i < limit; i++) {
            int byteRead = limitedStream.read();
            assertEquals(testData[i], (byte) byteRead, "Byte at position " + i + " should match");
        }
        
        // After limit, should return -1 (end of stream)
        assertEquals(-1, limitedStream.read(), "Should return -1 after reaching limit");
        
        limitedStream.close();
    }

    @Test
    @DisplayName("read(byte[], int, int) should read multiple bytes until limit is reached")
    void testReadByteArrayWithLimit() throws IOException {
        // Arrange
        InputStream baseStream = new ByteArrayInputStream(testData);
        long limit = 10; // Limit to first 10 bytes
        LimitedInputStream limitedStream = new LimitedInputStream(baseStream, limit);
        
        // Act
        byte[] buffer1 = new byte[5];
        int bytesRead1 = limitedStream.read(buffer1, 0, 5); // Read first 5 bytes
        byte[] expected1 = Arrays.copyOfRange(testData, 0, 5);
        
        byte[] buffer2 = new byte[5];
        int bytesRead2 = limitedStream.read(buffer2, 0, 5); // Read next 5 bytes
        byte[] expected2 = Arrays.copyOfRange(testData, 5, 10);
        
        byte[] buffer3 = new byte[5];
        int bytesRead3 = limitedStream.read(buffer3, 0, 5); // Try to read beyond limit
        
        // Assert
        assertEquals(5, bytesRead1, "First read should return 5 bytes");
        assertArrayEquals(expected1, buffer1, "First 5 bytes should match");
        
        assertEquals(5, bytesRead2, "Second read should return 5 bytes");
        assertArrayEquals(expected2, buffer2, "Next 5 bytes should match");
        
        assertEquals(-1, bytesRead3, "Third read should return -1 (end of stream)");
        
        limitedStream.close();
    }

    @Test
    @DisplayName("read(byte[], int, int) should handle partial reads correctly")
    void testPartialReadWithLimit() throws IOException {
        // Arrange
        InputStream baseStream = new ByteArrayInputStream(testData);
        long limit = 7; // Limit to 7 bytes
        LimitedInputStream limitedStream = new LimitedInputStream(baseStream, limit);
        
        // Act - Read 5 bytes first
        byte[] buffer1 = new byte[5];
        int bytesRead1 = limitedStream.read(buffer1, 0, 5);
        
        // Now try to read 5 more (but only 2 remain before limit)
        byte[] buffer2 = new byte[5];
        int bytesRead2 = limitedStream.read(buffer2, 0, 5);
        
        // Assert
        assertEquals(5, bytesRead1, "First read should return 5 bytes");
        assertEquals(2, bytesRead2, "Second read should return only 2 bytes");
        
        // Verify the content of the second buffer (only first 2 bytes should be from stream)
        assertEquals(testData[5], buffer2[0], "First byte of second read should match");
        assertEquals(testData[6], buffer2[1], "Second byte of second read should match");
        
        limitedStream.close();
    }

    @Test
    @DisplayName("read() should work with a limit larger than available data")
    void testReadWithLimitLargerThanData() throws IOException {
        // Arrange
        InputStream baseStream = new ByteArrayInputStream(testData);
        long limit = testData.length + 10; // Limit larger than data
        LimitedInputStream limitedStream = new LimitedInputStream(baseStream, limit);
        
        // Act & Assert
        for (int i = 0; i < testData.length; i++) {
            int byteRead = limitedStream.read();
            assertEquals(testData[i], (byte) byteRead, "Byte at position " + i + " should match");
        }
        
        // After data is exhausted, should return -1 (even though limit not reached)
        assertEquals(-1, limitedStream.read(), "Should return -1 after data is exhausted");
        
        limitedStream.close();
    }

    @Test
    @DisplayName("read(byte[], int, int) should handle zero-byte reads correctly")
    void testZeroByteRead() throws IOException {
        // Arrange
        InputStream baseStream = new ByteArrayInputStream(testData);
        long limit = 10;
        LimitedInputStream limitedStream = new LimitedInputStream(baseStream, limit);
        
        // Act
        byte[] buffer = new byte[5];
        int bytesRead = limitedStream.read(buffer, 0, 0); // Read 0 bytes
        
        // Assert
        assertEquals(0, bytesRead, "Zero-byte read should return 0");
        
        // Limit should not be affected
        bytesRead = limitedStream.read(buffer, 0, 5);
        assertEquals(5, bytesRead, "Should still be able to read 5 bytes");
        
        limitedStream.close();
    }

    @Test
    @DisplayName("read(byte[], int, int) should handle offsets correctly")
    void testReadWithOffset() throws IOException {
        // Arrange
        InputStream baseStream = new ByteArrayInputStream(testData);
        long limit = 10;
        LimitedInputStream limitedStream = new LimitedInputStream(baseStream, limit);
        
        // Act
        byte[] buffer = new byte[10]; // Buffer larger than needed
        int bytesRead = limitedStream.read(buffer, 2, 5); // Read 5 bytes starting at offset 2
        
        // Assert
        assertEquals(5, bytesRead, "Should read 5 bytes");
        
        // First two bytes should be unchanged (0)
        assertEquals(0, buffer[0], "First byte should be unchanged");
        assertEquals(0, buffer[1], "Second byte should be unchanged");
        
        // Next 5 bytes should be from the stream
        for (int i = 0; i < 5; i++) {
            assertEquals(testData[i], buffer[i+2], "Byte at buffer position " + (i+2) + " should match data");
        }
        
        // Last three bytes should be unchanged
        assertEquals(0, buffer[7], "Eighth byte should be unchanged");
        assertEquals(0, buffer[8], "Ninth byte should be unchanged");
        assertEquals(0, buffer[9], "Tenth byte should be unchanged");
        
        limitedStream.close();
    }

    @Test
    @DisplayName("close() should close the underlying stream")
    void testClose() throws IOException {
        // Arrange
        InputStream mockBaseStream = new ByteArrayInputStream(testData) {
            private boolean closed = false;
            
            @Override
            public void close() throws IOException {
                closed = true;
                super.close();
            }
            
            @Override
            public int read() {
                if (closed) {
                    return -1;
                }
                return super.read();
            }
        };
        
        LimitedInputStream limitedStream = new LimitedInputStream(mockBaseStream, 10);
        
        // Act
        limitedStream.close();
        
        // Assert - should throw exception when reading from closed stream
        assertEquals(limitedStream.read(), -1, "Should return -1 after closing");
    }

    @Test
    @DisplayName("read(byte[], int, int) should validate parameters correctly")
    void testParameterValidation() throws IOException {
        // Arrange
        InputStream baseStream = new ByteArrayInputStream(testData);
        LimitedInputStream limitedStream = new LimitedInputStream(baseStream, 10);
        byte[] buffer = new byte[5];
        
        // Assert - These should throw the same exceptions as InputStream would
        assertThrows(NullPointerException.class, 
                    () -> limitedStream.read(null, 0, 5),
                    "Should throw NullPointerException for null buffer");
                    
        assertThrows(IndexOutOfBoundsException.class, 
                    () -> limitedStream.read(buffer, -1, 5),
                    "Should throw IndexOutOfBoundsException for negative offset");
                    
        assertThrows(IndexOutOfBoundsException.class, 
                    () -> limitedStream.read(buffer, 0, -1),
                    "Should throw IndexOutOfBoundsException for negative length");
                    
        assertThrows(IndexOutOfBoundsException.class, 
                    () -> limitedStream.read(buffer, 4, 2),
                    "Should throw IndexOutOfBoundsException for offset+length > buffer.length");
                    
        limitedStream.close();
    }
}