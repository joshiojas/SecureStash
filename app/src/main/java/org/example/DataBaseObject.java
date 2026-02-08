package org.example;

/**
 * Immutable data object representing a database record for a tracked file.
 * 
 * <p>This class is a simple data transfer object that holds information
 * retrieved from the database about uploaded files.</p>
 * 
 * @author SecureStash Team
 * @version 1.0
 */
public class DataBaseObject {

    /** File path (primary key in database) */
    public final String name;
    
    /** Storage tier (currently unused) */
    public final String tier;
    
    /** Flag indicating if the file has been uploaded */
    public final boolean uploaded;
    
    /** MD5 checksum of the file */
    public final String md5_checksum;

    /**
     * Constructs a new DataBaseObject with the specified values.
     * 
     * @param name the file path
     * @param tier the storage tier (can be null)
     * @param uploaded whether the file has been uploaded
     * @param md5_checksum the MD5 checksum of the file
     */
    DataBaseObject(String name, String tier, boolean uploaded, String md5_checksum){
        this.name = name;
        this.tier = tier;
        this.uploaded = uploaded;
        this.md5_checksum = md5_checksum;
    }

    /**
     * Returns a comma-separated string representation of this object.
     * 
     * @return string in format "name,tier,uploaded,md5_checksum"
     */
    @Override
    public String toString(){
        return name + "," + tier + "," + uploaded + "," + md5_checksum;
    }
}
