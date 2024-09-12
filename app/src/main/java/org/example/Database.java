package org.example;

import javax.management.ObjectName;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class Database {
    String dburl = "";
    Database(String base_url){
        this.dburl = "jdbc:sqlite:" + base_url + "/history.db";

        boolean db = new File(base_url + "/history.db").exists();
        if (!db){
            create_db();
        }
    }

    private void create_db() {

        try (var conn = DriverManager.getConnection(dburl)) {
            if (conn != null) {
                var meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void connect(){
        Connection conn = null;

        try {

            conn = DriverManager.getConnection(dburl);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    public void createTable(String baseurl){
        String[] a = baseurl.split("/");
        String name = a[a.length-1];

        var sql = "CREATE TABLE IF NOT EXISTS details ("
                + "	name text NOT NULL PRIMARY KEY,"
                + " tier text,"
                + "uploaded boolean,"
                + "md5_checksum text"
                + ");";

        try (var conn = DriverManager.getConnection(dburl);
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    public void insertFile(FileDetails file){

        if (checkUpload(file)){
            return;
        }

        String sql = "INSERT INTO details(name,tier,uploaded,md5_checksum) VALUES(?,?,?,?)";
        try (var conn = DriverManager.getConnection(dburl);
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, file.getFilePath());
            pstmt.setString(2, null);
            pstmt.setBoolean(3, false);
            pstmt.setString(4, file.getChecksum());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    public ArrayList<DataBaseObject> getAllFiles(){

        String sql = "select * from details";
        ArrayList<DataBaseObject> arr = new ArrayList<DataBaseObject>();
        try (var conn = DriverManager.getConnection(dburl);
             Statement pstmt = conn.createStatement();
             ResultSet rs = pstmt.executeQuery(sql)
             ) {

            while (rs.next()){
                String name = rs.getString("name");
                String tier = rs.getString("tier");
                boolean uploaded = rs.getBoolean("uploaded");
                String checksum_md5 = rs.getString("md5_checksum");
                arr.add(new DataBaseObject(name,tier,uploaded, checksum_md5));
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return arr;
    }

    public void uploadFile(FileDetails file){


        String sql = "update details set uploaded=true where name=?";

        try (var conn = DriverManager.getConnection(dburl);
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, file.getFilePath());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkUpload(FileDetails file){
        String checkFile = "select * from details where name=?";
//        ArrayList<DataBaseObject> arr = new ArrayList<DataBaseObject>();

        try (var conn = DriverManager.getConnection(dburl);
             var pstmt = conn.prepareStatement(checkFile);
        ) {
            pstmt.setString(1,file.getFilePath());
            ResultSet rs = pstmt.executeQuery(checkFile);
            while(rs.next()){
                String name = rs.getString("name");
                boolean uploaded = rs.getBoolean("uploaded");
                String md5_checksum = rs.getString("md5_checksum");
                System.out.println(name + uploaded + md5_checksum);
                if (Objects.equals(name, file.getFilePath()) && uploaded && Objects.equals(md5_checksum,file.getChecksum())){
                    System.out.println("Not Uploading File since it's already uploaded");
                    return false;
                }
            }

        } catch (SQLException | NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

}