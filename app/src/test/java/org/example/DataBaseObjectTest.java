package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DataBaseObject class.
 */
class DataBaseObjectTest {

    @Test
    void testConstructor() {
        String name = "/path/to/file.txt";
        String tier = "STANDARD";
        boolean uploaded = true;
        String checksum = "abc123def456";
        
        DataBaseObject obj = new DataBaseObject(name, tier, uploaded, checksum);
        
        assertNotNull(obj);
        assertEquals(name, obj.name);
        assertEquals(tier, obj.tier);
        assertEquals(uploaded, obj.uploaded);
        assertEquals(checksum, obj.md5_checksum);
    }

    @Test
    void testConstructorWithNullTier() {
        String name = "/path/to/file.txt";
        String tier = null;
        boolean uploaded = false;
        String checksum = "abc123";
        
        DataBaseObject obj = new DataBaseObject(name, tier, uploaded, checksum);
        
        assertNotNull(obj);
        assertEquals(name, obj.name);
        assertNull(obj.tier);
        assertFalse(obj.uploaded);
        assertEquals(checksum, obj.md5_checksum);
    }

    @Test
    void testToString() {
        String name = "/path/to/file.txt";
        String tier = "STANDARD";
        boolean uploaded = true;
        String checksum = "abc123def456";
        
        DataBaseObject obj = new DataBaseObject(name, tier, uploaded, checksum);
        String result = obj.toString();
        
        assertNotNull(result);
        assertTrue(result.contains(name));
        assertTrue(result.contains(tier));
        assertTrue(result.contains("true"));
        assertTrue(result.contains(checksum));
        
        // Check the exact format
        assertEquals(name + "," + tier + "," + uploaded + "," + checksum, result);
    }

    @Test
    void testToStringWithNullTier() {
        String name = "/path/to/file.txt";
        String tier = null;
        boolean uploaded = false;
        String checksum = "xyz789";
        
        DataBaseObject obj = new DataBaseObject(name, tier, uploaded, checksum);
        String result = obj.toString();
        
        assertNotNull(result);
        assertEquals(name + "," + tier + "," + uploaded + "," + checksum, result);
    }

    @Test
    void testImmutability() {
        String name = "/path/to/file.txt";
        String tier = "STANDARD";
        boolean uploaded = true;
        String checksum = "abc123";
        
        DataBaseObject obj = new DataBaseObject(name, tier, uploaded, checksum);
        
        // All fields are final, so they should not be modifiable
        // This is enforced at compile-time, but we can verify the values remain constant
        assertEquals(name, obj.name);
        assertEquals(tier, obj.tier);
        assertEquals(uploaded, obj.uploaded);
        assertEquals(checksum, obj.md5_checksum);
    }

    @Test
    void testDifferentUploadStates() {
        DataBaseObject uploaded = new DataBaseObject("/file1.txt", "STANDARD", true, "abc");
        DataBaseObject notUploaded = new DataBaseObject("/file2.txt", "STANDARD", false, "def");
        
        assertTrue(uploaded.uploaded);
        assertFalse(notUploaded.uploaded);
    }
}
