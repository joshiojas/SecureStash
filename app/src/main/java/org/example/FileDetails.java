package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class FileDetails {

    private final String filePath;
    private final String basePath;
    private final String completePath;
    private final String filename;
    FileDetails(String filepath, String base_path){

        this.filePath = filepath.substring(base_path.length());
        this.basePath = base_path;
        this.completePath = filepath;
        this.filename = new File(filepath).getName();
    }

    public String getBasePath() {
        return basePath;
    }

    public String getCompletePath() {
        return completePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public String toString(){
        return filePath;
    }

    public String getFilename(){
        return filename;
    }

    public File getAsFile(){

        return new File(completePath);
    }
    public String getChecksum() throws IOException, NoSuchAlgorithmException {

        File file = new File(completePath);
        FileInputStream fis = new FileInputStream(file);
        MessageDigest digest = MessageDigest.getInstance("md5");
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        fis.close();
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
