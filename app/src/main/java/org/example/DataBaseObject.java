package org.example;

public class DataBaseObject {

    public final String name;
    public final String tier;
    public final boolean uploaded;
    public final String md5_checksum;

    DataBaseObject(String name, String tier, boolean uploaded, String md5_checksum){
        this.name = name;
        this.tier = tier;
        this.uploaded = uploaded;
        this.md5_checksum = md5_checksum;
    }

    @Override
    public String toString(){
        return name + "," + tier + "," + uploaded + "," + md5_checksum;
    }
}
