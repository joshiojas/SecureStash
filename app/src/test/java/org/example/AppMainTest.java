package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Unit tests for App class.
 */
class AppMainTest {

    @TempDir
    Path tempDir;

    private App app;

    @BeforeEach
    void setUp() {
        app = new App();
    }

    @Test
    void testConstructor() {
        assertNotNull(app);
    }

    @Test
    void testGetFilesEmptyDirectory() {
        String dirPath = tempDir.toString();
        ArrayList<FileDetails> files = app.getFiles(dirPath, dirPath);
        
        assertNotNull(files);
        assertTrue(files.isEmpty());
    }

    @Test
    void testGetFilesSingleFile() throws IOException {
        File testFile = tempDir.resolve("test.txt").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("Test content");
        }
        
        String dirPath = tempDir.toString();
        ArrayList<FileDetails> files = app.getFiles(dirPath, dirPath);
        
        assertNotNull(files);
        assertEquals(1, files.size());
        assertEquals("test.txt", files.get(0).getFilename());
    }

    @Test
    void testGetFilesMultipleFiles() throws IOException {
        // Create multiple files
        for (int i = 0; i < 3; i++) {
            File testFile = tempDir.resolve("test" + i + ".txt").toFile();
            try (FileWriter writer = new FileWriter(testFile)) {
                writer.write("Test content " + i);
            }
        }
        
        String dirPath = tempDir.toString();
        ArrayList<FileDetails> files = app.getFiles(dirPath, dirPath);
        
        assertNotNull(files);
        assertEquals(3, files.size());
    }

    @Test
    void testGetFilesIgnoresHistoryDb() throws IOException {
        // Create regular file and history.db
        File testFile = tempDir.resolve("test.txt").toFile();
        File historyDb = tempDir.resolve("history.db").toFile();
        
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("Test content");
        }
        try (FileWriter writer = new FileWriter(historyDb)) {
            writer.write("Database content");
        }
        
        String dirPath = tempDir.toString();
        ArrayList<FileDetails> files = app.getFiles(dirPath, dirPath);
        
        // Should only get test.txt, not history.db
        assertNotNull(files);
        assertEquals(1, files.size());
        assertEquals("test.txt", files.get(0).getFilename());
    }

    @Test
    void testGetFilesIgnoresDsStore() throws IOException {
        // Create regular file and .DS_Store
        File testFile = tempDir.resolve("test.txt").toFile();
        File dsStore = tempDir.resolve(".DS_Store").toFile();
        
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("Test content");
        }
        try (FileWriter writer = new FileWriter(dsStore)) {
            writer.write("Mac metadata");
        }
        
        String dirPath = tempDir.toString();
        ArrayList<FileDetails> files = app.getFiles(dirPath, dirPath);
        
        // Should only get test.txt, not .DS_Store
        assertNotNull(files);
        assertEquals(1, files.size());
        assertEquals("test.txt", files.get(0).getFilename());
    }

    @Test
    void testGetFilesRecursive() throws IOException {
        // Create nested directory structure
        File subDir = tempDir.resolve("subdir").toFile();
        subDir.mkdir();
        
        File file1 = tempDir.resolve("file1.txt").toFile();
        File file2 = subDir.toPath().resolve("file2.txt").toFile();
        
        try (FileWriter writer = new FileWriter(file1)) {
            writer.write("Content 1");
        }
        try (FileWriter writer = new FileWriter(file2)) {
            writer.write("Content 2");
        }
        
        String dirPath = tempDir.toString();
        ArrayList<FileDetails> files = app.getFiles(dirPath, dirPath);
        
        assertNotNull(files);
        assertEquals(2, files.size());
        
        // Verify both files are found
        boolean foundFile1 = false;
        boolean foundFile2 = false;
        for (FileDetails fd : files) {
            if (fd.getFilename().equals("file1.txt")) foundFile1 = true;
            if (fd.getFilename().equals("file2.txt")) foundFile2 = true;
        }
        assertTrue(foundFile1);
        assertTrue(foundFile2);
    }

    @Test
    void testGetFilesWithNestedDirectories() throws IOException {
        // Create deeply nested structure
        File level1 = tempDir.resolve("level1").toFile();
        File level2 = level1.toPath().resolve("level2").toFile();
        level1.mkdir();
        level2.mkdir();
        
        File file1 = tempDir.resolve("root.txt").toFile();
        File file2 = level1.toPath().resolve("level1.txt").toFile();
        File file3 = level2.toPath().resolve("level2.txt").toFile();
        
        try (FileWriter writer = new FileWriter(file1)) {
            writer.write("Root");
        }
        try (FileWriter writer = new FileWriter(file2)) {
            writer.write("Level 1");
        }
        try (FileWriter writer = new FileWriter(file3)) {
            writer.write("Level 2");
        }
        
        String dirPath = tempDir.toString();
        ArrayList<FileDetails> files = app.getFiles(dirPath, dirPath);
        
        assertNotNull(files);
        assertEquals(3, files.size());
    }

    @Test
    void testGetFilesHandlesNullListFiles() {
        // Test with a file path (not directory) which returns null from listFiles()
        File testFile = tempDir.resolve("test.txt").toFile();
        try {
            testFile.createNewFile();
        } catch (IOException e) {
            fail("Failed to create test file");
        }
        
        String filePath = testFile.getAbsolutePath();
        ArrayList<FileDetails> files = app.getFiles(filePath, tempDir.toString());
        
        // Should return empty list when listFiles() returns null
        assertNotNull(files);
        assertTrue(files.isEmpty());
    }
}
