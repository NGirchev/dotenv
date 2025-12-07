package io.github.ngirchev.dotenv;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DotEnvLoader class
 */
class DotEnvLoaderTest {

    @TempDir
    Path tempDir;

    private Properties originalProperties;

    @BeforeEach
    void setUp() {
        // Save original system properties
        originalProperties = new Properties();
        originalProperties.putAll(System.getProperties());
    }

    @AfterEach
    void tearDown() {
        // Restore original system properties
        System.setProperties(originalProperties);
    }

    @Test
    void testLoadDotEnv_WithValidFile() throws IOException {
        // Create test .env file
        Path envFile = tempDir.resolve(".env");
        Files.write(envFile, 
            ("DB_HOST=localhost\n" +
             "DB_PORT=5432\n" +
             "API_KEY=test123\n").getBytes());

        // Load variables
        DotEnvLoader.loadDotEnv(envFile);

        // Verify that variables are loaded
        assertEquals("localhost", System.getProperty("DB_HOST"));
        assertEquals("5432", System.getProperty("DB_PORT"));
        assertEquals("test123", System.getProperty("API_KEY"));
    }

    @Test
    void testLoadDotEnv_WithComments() throws IOException {
        Path envFile = tempDir.resolve(".env");
        Files.write(envFile,
            ("# This is a comment\n" +
             "KEY1=value1\n" +
             "# Another comment\n" +
             "KEY2=value2\n").getBytes());

        DotEnvLoader.loadDotEnv(envFile);

        assertEquals("value1", System.getProperty("KEY1"));
        assertEquals("value2", System.getProperty("KEY2"));
        assertNull(System.getProperty("# This is a comment"));
    }

    @Test
    void testLoadDotEnv_WithEmptyLines() throws IOException {
        Path envFile = tempDir.resolve(".env");
        Files.write(envFile,
            ("KEY1=value1\n" +
             "\n" +
             "KEY2=value2\n" +
             "   \n" +
             "KEY3=value3\n").getBytes());

        DotEnvLoader.loadDotEnv(envFile);

        assertEquals("value1", System.getProperty("KEY1"));
        assertEquals("value2", System.getProperty("KEY2"));
        assertEquals("value3", System.getProperty("KEY3"));
    }

    @Test
    void testLoadDotEnv_WithSpaces() throws IOException {
        Path envFile = tempDir.resolve(".env");
        Files.write(envFile,
            ("  KEY1  =  value1  \n" +
             "KEY2=value2\n").getBytes());

        DotEnvLoader.loadDotEnv(envFile);

        assertEquals("value1", System.getProperty("KEY1"));
        assertEquals("value2", System.getProperty("KEY2"));
    }

    @Test
    void testLoadDotEnv_WithInvalidLines() throws IOException {
        Path envFile = tempDir.resolve(".env");
        Files.write(envFile,
            ("=value_without_key\n" +
             "KEY_WITHOUT_VALUE=\n" +
             "VALID_KEY=valid_value\n").getBytes());

        DotEnvLoader.loadDotEnv(envFile);

        // Invalid line (empty key) should be ignored
        // KEY_WITHOUT_VALUE= is valid and should be loaded with empty string value
        assertEquals("valid_value", System.getProperty("VALID_KEY"));
        assertEquals("", System.getProperty("KEY_WITHOUT_VALUE"));
    }

    @Test
    void testLoadDotEnv_FileNotFound() {
        Path nonExistentFile = tempDir.resolve("nonexistent.env");
        
        // Should not throw exception
        assertDoesNotThrow(() -> DotEnvLoader.loadDotEnv(nonExistentFile));
    }

    @Test
    void testLoadDotEnv_DoesNotOverwriteExistingProperties() throws IOException {
        // Set existing property
        System.setProperty("EXISTING_KEY", "existing_value");

        Path envFile = tempDir.resolve(".env");
        Files.write(envFile, "EXISTING_KEY=new_value\nNEW_KEY=new_value\n".getBytes());

        DotEnvLoader.loadDotEnv(envFile);

        // Existing property should not be overwritten
        assertEquals("existing_value", System.getProperty("EXISTING_KEY"));
        // New property should be loaded
        assertEquals("new_value", System.getProperty("NEW_KEY"));
    }

    @Test
    void testLoadDotEnv_DefaultPath() {
        // Test method without parameters (uses .env in project root)
        // This test may not work if .env file doesn't exist, but method should not fail
        assertDoesNotThrow(() -> DotEnvLoader.loadDotEnv());
    }

    @Test
    void testGetEnv_FromSystemProperty() {
        System.setProperty("TEST_KEY", "test_value");
        
        String value = DotEnvLoader.getEnv("TEST_KEY");
        
        assertEquals("test_value", value);
    }

    @Test
    void testGetEnv_FromEnvironment() {
        // Verify that method returns value from environment variables,
        // if it's not in System properties
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome != null) {
            // Clear from System properties if exists
            System.clearProperty("JAVA_HOME");
            String value = DotEnvLoader.getEnv("JAVA_HOME");
            assertEquals(javaHome, value);
        }
    }

    @Test
    void testGetEnv_NotFound() {
        String value = DotEnvLoader.getEnv("NON_EXISTENT_KEY_12345");
        assertNull(value);
    }

    @Test
    void testGetEnv_PrioritySystemPropertyOverEnv() {
        System.setProperty("PRIORITY_TEST", "system_property_value");
        
        String value = DotEnvLoader.getEnv("PRIORITY_TEST");
        
        // System property should have priority
        assertEquals("system_property_value", value);
    }
}

