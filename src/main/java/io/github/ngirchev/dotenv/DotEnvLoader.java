package io.github.ngirchev.dotenv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Utility class for loading environment variables from .env files into Java System properties.
 * 
 * <p>This class provides a simple way to manage application configuration by loading
 * key-value pairs from a `.env` file into System properties. The loaded properties can
 * then be accessed using {@link #getEnv(String)} or standard Java System property methods.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Loads variables from `.env` files into System properties</li>
 *   <li>Does not overwrite existing System properties or environment variables</li>
 *   <li>Supports comments (lines starting with #)</li>
 *   <li>Automatically trims whitespace from keys and values</li>
 *   <li>Silently handles missing files and invalid lines</li>
 * </ul>
 * 
 * <p><b>Example usage:</b>
 * <pre>{@code
 * // Load from default .env file in project root
 * DotEnvLoader.loadDotEnv();
 * 
 * // Access loaded variables
 * String dbHost = DotEnvLoader.getEnv("DB_HOST");
 * }</pre>
 * 
 * @author NGirchev
 * @since 1.0.0
 */
public final class DotEnvLoader {

    private static final Logger LOG = LoggerFactory.getLogger(DotEnvLoader.class);

    private DotEnvLoader() {
    }

    /**
     * Loads environment variables from a specified .env file into System properties.
     * 
     * <p>This method reads the `.env` file line by line and loads key-value pairs
     * into System properties. The following rules apply:
     * <ul>
     *   <li>Lines starting with '#' are treated as comments and ignored</li>
     *   <li>Empty lines are ignored</li>
     *   <li>Whitespace around keys and values is automatically trimmed</li>
     *   <li>Existing System properties and environment variables are never overwritten</li>
     *   <li>If the file doesn't exist, the method returns silently (logs debug message)</li>
     *   <li>Invalid lines (without '=' or with empty keys) are ignored</li>
     * </ul>
     * 
     * <p><b>Example .env file format:</b>
     * <pre>{@code
     * # Database configuration
     * DB_HOST=localhost
     * DB_PORT=5432
     * API_KEY=your-secret-key
     * }</pre>
     * 
     * <p><b>Example usage:</b>
     * <pre>{@code
     * import java.nio.file.Paths;
     * 
     * // Load from custom path
     * DotEnvLoader.loadDotEnv(Paths.get("config/.env"));
     * }</pre>
     *
     * @param envPath the path to the .env file to load. Must not be null.
     * @throws NullPointerException if envPath is null
     * @see #loadDotEnv()
     * @see #getEnv
     */
    public static void loadDotEnv(final Path envPath) {
        if (!Files.exists(envPath)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(".env file not found at {}, skipping dotenv loading", envPath);
            }
            return;
        }

        try (Stream<String> lines = Files.lines(envPath)) {
            lines.map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .filter(line -> line.length() > 0 && line.charAt(0) != '#')
                    .forEach(line -> {
                        final int idx = line.indexOf('=');
                        if (idx <= 0) {
                            return;
                        }
                        final String key = line.substring(0, idx).trim();

                        if (key.isEmpty()) {
                            return;
                        }

                        final String value = line.substring(idx + 1).trim();

                        // Do not overwrite existing values
                        if (System.getProperty(key) == null && System.getenv(key) == null) {
                            System.setProperty(key, value);
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Loaded .env property: {}", key);
                            }
                        }
                    });
            if (LOG.isInfoEnabled()) {
                LOG.info(".env properties loaded into System properties from {}", envPath);
            }
        } catch (IOException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Failed to load .env file from {}: {}. Skipping dotenv loading.", envPath, e.getMessage());
            }
        }
    }

    /**
     * Loads environment variables from the default `.env` file in the project root directory.
     * 
     * <p>This is a convenience method that calls {@link #loadDotEnv(Path)} with
     * the path to `.env` in the current working directory (project root).
     * 
     * <p><b>Example usage:</b>
     * <pre><code>
     * public class MyApp {
     *     public static void main(String[] args) {
     *         // Load from default .env file
     *         DotEnvLoader.loadDotEnv();
     *         
     *         // Use loaded variables
     *         String apiKey = DotEnvLoader.getEnv("API_KEY");
     *     }
     * }</code></pre>
     */
    public static void loadDotEnv() {
        loadDotEnv(Paths.get(".env"));
    }

    /**
     * Gets an environment variable value from System properties or system environment variables.
     * 
     * <p>This method checks for the variable in the following order:
     * <ol>
     *   <li>System properties (loaded from `.env` file via {@link #loadDotEnv()})</li>
     *   <li>System environment variables</li>
     * </ol>
     * 
     * <p>If the variable is found in System properties, that value is returned.
     * Otherwise, the method checks system environment variables. If the variable
     * is not found in either location, {@code null} is returned.
     * 
     * <p><b>Example usage:</b>
     * <pre>{@code
     * // Load .env file first
     * DotEnvLoader.loadDotEnv();
     * 
     * // Get variable value
     * String dbHost = DotEnvLoader.getEnv("DB_HOST");
     * if (dbHost != null) {
     *     System.out.println("Database host: " + dbHost);
     * } else {
     *     System.out.println("DB_HOST not set");
     * }
     * }</pre>
     *
     * @param key the name of the environment variable to retrieve. Must not be null.
     * @return the value of the environment variable, or {@code null} if not found
     * @throws NullPointerException if key is null
     * @see System#getProperty(String)
     * @see System#getenv(String)
     */
    public static String getEnv(final String key) {
        final String value = System.getProperty(key);
        final String result;
        if (value != null) {
            result = value;
        } else {
            result = System.getenv(key);
        }
        return result;
    }
}

