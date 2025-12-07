# Contributing

Thank you for your interest in contributing to this project! We welcome any contributions.

## Development

### Requirements

- Java 11 or higher
- Maven 3.6+

### Setup

```shell
git clone https://github.com/YOUR_USERNAME/dotenv.git
cd dotenv
mvn clean install
```

### Making Changes

1. Create a branch: `git checkout -b feature/your-feature-name`
2. Make your changes
3. Run tests: `mvn test`
4. Commit and push: `git push origin feature/your-feature-name`
5. Create a Pull Request on GitHub

## Release Process

_Note: Only maintainers can publish releases._

### Prepare Release (version and tag)

```shell
# Interactive (will prompt for versions)
mvn -B release:prepare -Darguments="-DskipTests"

# Or specify versions explicitly
mvn -B release:prepare \
  -DreleaseVersion=1.0.0 \
  -DdevelopmentVersion=1.0.1-SNAPSHOT \
  -Darguments="-DskipTests"
```

This will:
- Remove `-SNAPSHOT` from current version
- Create a git tag
- Update version to next development version

#### 2. Switch to Release Tag

```shell
git checkout v1.0.0
```

#### 3. Deploy to Maven Central

```shell
mvn clean deploy -P release -DskipTests
```

**Important:** Do NOT use `release:perform`. Use `mvn clean deploy -P release` instead.

### Staging Repository

After deploy, close and release from [Sonatype Central Portal](https://central.sonatype.com/):

1. Navigate to Staging Repositories
2. Select repository (look for `io.github.ngirchev`)
3. Click `Close`
4. Click `Release`

### GitHub Release

After successful deployment:

```shell
# Create GitHub release
gh release create v1.0.0 -F CHANGELOG.md

# Attach signed artifacts (for OpenSSF Security Score)
gh release upload v1.0.0 target/*.jar.asc --clobber
```

## GPG Setup

### Configure GPG

Add to `~/.gnupg/gpg.conf`:

```
auto-key-retrieve
no-emit-version
keyserver https://keys.openpgp.org
```

### Publish GPG Key

```shell
# Send public key (recommended: keys.openpgp.org)
gpg --keyserver https://keys.openpgp.org --send-keys YOUR_KEY_ID

# Verify key is published
gpg --keyserver https://keys.openpgp.org --search-keys YOUR_KEY_ID
```

### Configure Maven settings.xml

Add configuration to **global** `~/.m2/settings.xml` (not in project directory):

**1. OSSRH Server Credentials (for Central Portal):**

Get your credentials from Central Portal:
1. Log in to [Central Portal](https://central.sonatype.com/)
2. Click on your profile → "View Account"
3. Click "Generate User Token" (or "User Token" if already generated)
4. Copy the token

```xml
<servers>
  <server>
    <id>ossrh</id>
    <username>YOUR_GITHUB_USERNAME</username>
    <!-- Use the User Token from Central Portal as password -->
    <password>YOUR_USER_TOKEN</password>
  </server>
</servers>
```

**Note:** 
- `username` = your GitHub username (e.g., `NGirchev`)
- `password` = User Token from Central Portal (not your GitHub password!)
- You can encrypt the token: `mvn --encrypt-password`

**2. GPG Configuration:**

```xml
<settings>
  <profiles>
    <profile>
      <id>gpg</id>
      <properties>
        <gpg.executable>gpg</gpg.executable>
        <!-- Specify which key to use (your key ID) -->
        <gpg.keyname>YOUR_KEY_ID</gpg.keyname>
        <!-- Use GPG agent (recommended) - no password in file -->
        <!-- Or use environment variable: <gpg.passphrase>${env.GPG_PASSPHRASE}</gpg.passphrase> -->
      </properties>
    </profile>
  </profiles>
</settings>
```

**Security options (choose one):**

1. **GPG Agent (Recommended)** - No password in file:
   - GPG agent will prompt for password once and cache it
   - Most secure option
   - Just run: `gpg-agent --daemon` before deployment

2. **Environment Variable** - Set before deployment:
   ```shell
   export GPG_PASSPHRASE=your_passphrase
   ```
   Then use in settings.xml: `<gpg.passphrase>${env.GPG_PASSPHRASE}</gpg.passphrase>`

3. **Maven Encrypted Password** - Encrypt password with Maven:
   ```shell
   mvn --encrypt-password
   ```
   Then use encrypted value in settings.xml

## Resources

- [Publish Guide](https://central.sonatype.com/publish/publish-guide/)
- [GPG Setup](https://central.sonatype.com/publish/requirements/gpg/)
- [Central Portal](https://central.sonatype.com/)
- [OpenSSF Security Scorecard](https://api.securityscorecards.dev/#/results/getResult) - Enter: `platform=github.com`, `org=NGirchev`, `repo=dotenv`

## Code Style

- Follow standard Java conventions
- Use meaningful variable and method names
- Add comments where necessary
- Ensure all tests pass

## Questions?

If you have any questions, please create an Issue on GitHub.
