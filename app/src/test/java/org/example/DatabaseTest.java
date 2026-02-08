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
import java.util.ArrayList;

/**
 * Unit tests for Database class.
 */
class DatabaseTest {

    @TempDir
    Path tempDir;

    private Database database;
    private String basePath;

    @BeforeEach
    void setUp() {
        basePath = tempDir.toString();
        database = new Database(basePath);
        database.connect();
        database.createTable(basePath);
    }

    @AfterEach
    void tearDown() {
        // Clean up database file
        File dbFile = new File(basePath + "/history.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    void testDatabaseCreation() {
        File dbFile = new File(basePath + "/history.db");
        assertTrue(dbFile.exists(), "Database file should be created");
    }

    @Test
    void testConnect() {
        // Just verify no exception is thrown
        assertDoesNotThrow(() -> database.connect());
    }

    @Test
    void testCreateTable() {
        // Verify table creation doesn't throw exception
        assertDoesNotThrow(() -> database.createTable(basePath));
    }

    @Test
    void testInsertFile() throws IOException, NoSuchAlgorithmException {
        // Create a test file
        File testFile = tempDir.resolve("test.txt").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("Test content");
        }
        
        FileDetails fileDetails = new FileDetails(testFile.getAbsolutePath(), basePath);
        
        // Insert the file
        assertDoesNotThrow(() -> database.insertFile(fileDetails));
        
        // Verify it was inserted
        ArrayList<DataBaseObject> allFiles = database.getAllFiles();
        assertFalse(allFiles.isEmpty());
        assertEquals(1, allFiles.size());
        assertEquals(fileDetails.getFilePath(), allFiles.get(0).name);
    }

    @Test
    void testCheckUploadForNewFile() throws IOException, NoSuchAlgorithmException {
        File testFile = tempDir.resolve("test.txt").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("Test content");
        }
        
        FileDetails fileDetails = new FileDetails(testFile.getAbsolutePath(), basePath);
        
        // New file should return true (needs upload)
        assertTrue(database.checkUpload(fileDetails));
    }

    @Test
    void testCheckUploadForUploadedFile() throws IOException, NoSuchAlgorithmException {
        File testFile = tempDir.resolve("test.txt").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("Test content");
        }
        
        FileDetails fileDetails = new FileDetails(testFile.getAbsolutePath(), basePath);
        
        // Insert and mark as uploaded
        database.insertFile(fileDetails);
        database.uploadFile(fileDetails);
        
        // Already uploaded file with same checksum should return false (no upload needed)
        assertFalse(database.checkUpload(fileDetails));
    }

    @Test
    void testCheckUploadForModifiedFile() throws IOException, NoSuchAlgorithmException {
        File testFile = tempDir.resolve("test.txt").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("Original content");
        }
        
        FileDetails fileDetails1 = new FileDetails(testFile.getAbsolutePath(), basePath);
        
        // Insert and mark as uploaded
        database.insertFile(fileDetails1);
        database.uploadFile(fileDetails1);
        
        // Modify the file
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("Modified content - different from original");
        }
        
        FileDetails fileDetails2 = new FileDetails(testFile.getAbsolutePath(), basePath);
        
        // Modified file should return true (needs upload) because checksum changed
        assertTrue(database.checkUpload(fileDetails2));
    }

    @Test
    void testUploadFile() throws IOException, NoSuchAlgorithmException {
        File testFile = tempDir.resolve("test.txt").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("Test content");
        }
        
        FileDetails fileDetails = new FileDetails(testFile.getAbsolutePath(), basePath);
        
        // Insert the file
        database.insertFile(fileDetails);
        
        // Verify it's not uploaded
        ArrayList<DataBaseObject> files = database.getAllFiles();
        assertFalse(files.get(0).uploaded);
        
        // Mark as uploaded
        database.uploadFile(fileDetails);
        
        // Verify it's now uploaded
        files = database.getAllFiles();
        assertTrue(files.get(0).uploaded);
    }

    @Test
    void testGetAllFilesEmpty() {
        ArrayList<DataBaseObject> files = database.getAllFiles();
        assertNotNull(files);
        assertTrue(files.isEmpty());
    }

    @Test
    void testGetAllFilesWithMultipleFiles() throws IOException, NoSuchAlgorithmException {
        // Create and insert multiple files
        for (int i = 0; i < 3; i++) {
            File testFile = tempDir.resolve("test" + i + ".txt").toFile();
            try (FileWriter writer = new FileWriter(testFile)) {
                writer.write("Test content " + i);
            }
            
            FileDetails fileDetails = new FileDetails(testFile.getAbsolutePath(), basePath);
            database.insertFile(fileDetails);
        }
        
        ArrayList<DataBaseObject> files = database.getAllFiles();
        assertNotNull(files);
        assertEquals(3, files.size());
    }

    @Test
    void testInsertDuplicateFile() throws IOException, NoSuchAlgorithmException {
        File testFile = tempDir.resolve("test.txt").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("Test content");
        }
        
        FileDetails fileDetails = new FileDetails(testFile.getAbsolutePath(), basePath);
        
        // Insert the file twice
        database.insertFile(fileDetails);
        database.uploadFile(fileDetails);
        
        // Second insert should not add a duplicate (due to checkUpload check)
        database.insertFile(fileDetails);
        
        ArrayList<DataBaseObject> files = database.getAllFiles();
        assertEquals(1, files.size());
    }
}
