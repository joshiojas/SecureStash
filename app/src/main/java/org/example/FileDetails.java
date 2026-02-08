package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Represents metadata and operations for a file to be backed up.
 * 
 * <p>This class encapsulates file path information and provides utilities
 * for calculating checksums and accessing file data.</p>
 * 
 * @author SecureStash Team
 * @version 1.0
 */
public class FileDetails {

    /** Relative file path from the base path */
    private final String filePath;
    
    /** Base path used for calculating relative paths */
    private final String basePath;
    
    /** Complete absolute path to the file */
    private final String completePath;
    
    /** Just the filename without path */
    private final String filename;
    
    /**
     * Constructs a FileDetails object.
     * 
     * @param filepath the absolute path to the file
     * @param base_path the base path for calculating relative paths
     */
    FileDetails(String filepath, String base_path){

        this.filePath = filepath.substring(base_path.length());
        this.basePath = base_path;
        this.completePath = filepath;
        this.filename = new File(filepath).getName();
    }

    /**
     * Gets the base path.
     * 
     * @return the base path string
     */
    public String getBasePath() {
        return basePath;
    }

    /**
     * Gets the complete absolute path.
     * 
     * @return the complete file path
     */
    public String getCompletePath() {
        return completePath;
    }

    /**
     * Gets the relative file path from the base path.
     * 
     * @return the relative file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Returns the relative file path as a string representation.
     * 
     * @return the relative file path
     */
    public String toString(){
        return filePath;
    }

    /**
     * Gets just the filename without the path.
     * 
     * @return the filename
     */
    public String getFilename(){
        return filename;
    }

    /**
     * Gets the file as a File object.
     * 
     * @return File object representing this file
     */
    public File getAsFile(){

        return new File(completePath);
    }
    /**
     * Calculates MD5 checksum for the file.
     * 
     * @return MD5 checksum as a hexadecimal string
     * @throws IOException if there's an error reading the file
     * @throws NoSuchAlgorithmException if MD5 algorithm is not available
     */
    public String getChecksum() throws IOException, NoSuchAlgorithmException {

        File file = new File(completePath);
        MessageDigest digest = MessageDigest.getInstance("MD5");
        
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] byteArray = new byte[1024];
            int bytesCount = 0;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }
        
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
