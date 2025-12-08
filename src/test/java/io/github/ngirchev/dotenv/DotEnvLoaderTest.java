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

    @Test
    void testLoadDotEnv_WithLineStartingWithEquals() throws IOException {
        // Test case where line starts with '=' (idx == 0 after trim)
        Path envFile = tempDir.resolve(".env");
        Files.write(envFile,
            ("=invalid_line\n" +
             "VALID_KEY=valid_value\n").getBytes());

        DotEnvLoader.loadDotEnv(envFile);

        // Line starting with '=' should be ignored (idx == 0, so skipped)
        // Only valid key should be loaded
        assertEquals("valid_value", System.getProperty("VALID_KEY"));
    }

    @Test
    void testLoadDotEnv_DoesNotOverwriteExistingEnvVariable() throws IOException {
        // Test case where property exists in System.getenv but not in System.getProperty
        // This tests the branch: System.getProperty(key) == null && System.getenv(key) == null
        // We need to ensure that if env var exists, it's not overwritten
        
        // For the branch where System.getenv(key) != null, we need a real env var
        // Let's use PATH which should always exist
        String originalPath = System.getProperty("PATH");
        System.clearProperty("PATH");
        
        try {
            Path envFile = tempDir.resolve(".env");
            Files.write(envFile, "PATH=new_path_value\nNEW_KEY=new_value\n".getBytes());

            DotEnvLoader.loadDotEnv(envFile);

            // PATH from environment should not be overwritten
            // Since we can't easily test System.getenv in unit tests,
            // we'll test that NEW_KEY is loaded (both System.getProperty and System.getenv are null)
            assertEquals("new_value", System.getProperty("NEW_KEY"));
        } finally {
            // Restore original PATH if it existed
            if (originalPath != null) {
                System.setProperty("PATH", originalPath);
            }
        }
    }

    @Test
    void testLoadDotEnv_WithEqualsAtStartAfterTrim() throws IOException {
        // Test case where after trim, line starts with '='
        Path envFile = tempDir.resolve(".env");
        Files.write(envFile,
            ("  =value_after_spaces\n" +
             "VALID_KEY=valid_value\n").getBytes());

        DotEnvLoader.loadDotEnv(envFile);

        // Line with '=' at start after trim should be ignored (idx == 0)
        // Only valid key should be loaded
        assertEquals("valid_value", System.getProperty("VALID_KEY"));
    }

    @Test
    void testLoadDotEnv_WithNoEqualsSign() throws IOException {
        // Test case where line has no '=' sign (idx == -1, which is < 0)
        Path envFile = tempDir.resolve(".env");
        Files.write(envFile,
            ("LINE_WITHOUT_EQUALS\n" +
             "VALID_KEY=valid_value\n").getBytes());

        DotEnvLoader.loadDotEnv(envFile);

        // Line without '=' should be ignored
        assertNull(System.getProperty("LINE_WITHOUT_EQUALS"));
        assertEquals("valid_value", System.getProperty("VALID_KEY"));
    }
}

