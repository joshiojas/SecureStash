package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

/**
 * Unit tests for FileDetails class.
 */
class FileDetailsTest {

    @TempDir
    Path tempDir;

    private File testFile;
    private String testContent = "Test file content for checksum calculation";

    @BeforeEach
    void setUp() throws IOException {
        testFile = tempDir.resolve("test.txt").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write(testContent);
        }
    }

    @Test
    void testConstructor() {
        String basePath = tempDir.toString();
        String filePath = testFile.getAbsolutePath();
        
        FileDetails fileDetails = new FileDetails(filePath, basePath);
        
        assertNotNull(fileDetails);
        assertEquals(basePath, fileDetails.getBasePath());
        assertEquals(filePath, fileDetails.getCompletePath());
        assertEquals("test.txt", fileDetails.getFilename());
    }

    @Test
    void testGetFilePath() {
        String basePath = tempDir.toString();
        String filePath = testFile.getAbsolutePath();
        
        FileDetails fileDetails = new FileDetails(filePath, basePath);
        String relativePath = fileDetails.getFilePath();
        
        // The relative path should be the file path minus the base path
        assertTrue(relativePath.endsWith("test.txt"));
        assertFalse(relativePath.equals(filePath));
    }

    @Test
    void testGetAsFile() {
        String basePath = tempDir.toString();
        String filePath = testFile.getAbsolutePath();
        
        FileDetails fileDetails = new FileDetails(filePath, basePath);
        File file = fileDetails.getAsFile();
        
        assertNotNull(file);
        assertTrue(file.exists());
        assertEquals(testFile.getAbsolutePath(), file.getAbsolutePath());
    }

    @Test
    void testGetChecksum() throws IOException, NoSuchAlgorithmException {
        String basePath = tempDir.toString();
        String filePath = testFile.getAbsolutePath();
        
        FileDetails fileDetails = new FileDetails(filePath, basePath);
        String checksum = fileDetails.getChecksum();
        
        assertNotNull(checksum);
        assertFalse(checksum.isEmpty());
        assertEquals(32, checksum.length()); // MD5 produces 32 hex characters
        
        // Verify checksum is consistent
        String checksum2 = fileDetails.getChecksum();
        assertEquals(checksum, checksum2);
    }

    @Test
    void testGetChecksumDifferentContent() throws IOException, NoSuchAlgorithmException {
        String basePath = tempDir.toString();
        
        // Create two files with different content
        File file1 = tempDir.resolve("file1.txt").toFile();
        File file2 = tempDir.resolve("file2.txt").toFile();
        
        try (FileWriter writer = new FileWriter(file1)) {
            writer.write("Content 1");
        }
        try (FileWriter writer = new FileWriter(file2)) {
            writer.write("Content 2");
        }
        
        FileDetails fileDetails1 = new FileDetails(file1.getAbsolutePath(), basePath);
        FileDetails fileDetails2 = new FileDetails(file2.getAbsolutePath(), basePath);
        
        String checksum1 = fileDetails1.getChecksum();
        String checksum2 = fileDetails2.getChecksum();
        
        assertNotEquals(checksum1, checksum2);
    }

    @Test
    void testToString() {
        String basePath = tempDir.toString();
        String filePath = testFile.getAbsolutePath();
        
        FileDetails fileDetails = new FileDetails(filePath, basePath);
        String toString = fileDetails.toString();
        
        assertNotNull(toString);
        assertEquals(fileDetails.getFilePath(), toString);
    }

    @Test
    void testGetFilename() {
        String basePath = tempDir.toString();
        String filePath = testFile.getAbsolutePath();
        
        FileDetails fileDetails = new FileDetails(filePath, basePath);
        
        assertEquals("test.txt", fileDetails.getFilename());
    }
}
