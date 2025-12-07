package io.github.ngirchev.dotenv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Utility class for loading environment variables from .env file
 */
public class DotEnvLoader {

    private static final Logger log = LoggerFactory.getLogger(DotEnvLoader.class);

    /**
     * Loads environment variables from .env file into System properties.
     * Does not overwrite existing values in System properties or environment variables.
     *
     * @param envPath path to .env file (default is ".env" in project root)
     */
    public static void loadDotEnv(Path envPath) {
        if (!Files.exists(envPath)) {
            log.debug(".env file not found at {}, skipping dotenv loading", envPath);
            return;
        }

        try (Stream<String> lines = Files.lines(envPath)) {
            lines.map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .filter(line -> !line.startsWith("#"))
                    .forEach(line -> {
                        int idx = line.indexOf('=');
                        if (idx <= 0) {
                            return;
                        }
                        String key = line.substring(0, idx).trim();
                        String value = line.substring(idx + 1).trim();

                        if (key.isEmpty()) {
                            return;
                        }

                        // Do not overwrite existing values
                        if (System.getProperty(key) == null && System.getenv(key) == null) {
                            System.setProperty(key, value);
                            log.debug("Loaded .env property: {}", key);
                        }
                    });
            log.info(".env properties loaded into System properties from {}", envPath);
        } catch (IOException e) {
            log.warn("Failed to load .env file from {}: {}. Skipping dotenv loading.", envPath, e.getMessage());
        }
    }

    /**
     * Loads environment variables from .env file in project root.
     * Convenient method for default usage.
     */
    public static void loadDotEnv() {
        loadDotEnv(Paths.get(".env"));
    }

    /**
     * Gets environment variable value from System properties or environment variables.
     * First checks System properties (loaded from .env), then environment variables.
     *
     * @param key variable name
     * @return variable value or null if not found
     */
    public static String getEnv(String key) {
        String value = System.getProperty(key);
        if (value != null) {
            return value;
        }
        return System.getenv(key);
    }
}

