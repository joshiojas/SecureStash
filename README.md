# SecureStash

**SecureStash** is a robust and secure file backup utility that automatically uploads files from a local directory to Amazon S3. It tracks file changes using MD5 checksums and maintains a SQLite database to prevent redundant uploads.

## Features

- 🔒 **Secure AWS S3 Integration**: Seamlessly backs up files to Amazon S3 cloud storage
- 📊 **Smart Deduplication**: Uses MD5 checksums to detect and skip already-uploaded files
- 🗄️ **SQLite Database Tracking**: Maintains a local history of uploaded files
- 🔄 **Recursive Directory Scanning**: Automatically finds and backs up files in nested directories
- 🚫 **Intelligent Filtering**: Excludes system files like `.DS_Store` and database files
- ⚡ **Efficient Transfers**: Uses AWS Transfer Manager for optimized upload performance

## Table of Contents

- [Requirements](#requirements)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Architecture](#architecture)
- [API Documentation](#api-documentation)
- [Security](#security)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## Requirements

- **Java**: 21 or higher
- **Gradle**: 8.10 or higher (wrapper included)
- **AWS Account**: With S3 access and configured credentials
- **AWS CLI**: Configured with appropriate credentials (optional but recommended)

## Installation

### 1. Clone the Repository

```bash
git clone https://github.com/joshiojas/SecureStash.git
cd SecureStash
```

### 2. Build the Project

```bash
./gradlew build
```

### 3. Configure AWS Credentials

Ensure your AWS credentials are configured. You can use:

**Option A: AWS CLI Configuration**
```bash
aws configure
```

**Option B: Environment Variables**
```bash
export AWS_ACCESS_KEY_ID=your_access_key
export AWS_SECRET_ACCESS_KEY=your_secret_key
export AWS_DEFAULT_REGION=ap-southeast-1
```

**Option C: AWS Credentials File**
```
~/.aws/credentials
```

## Configuration

### AWS Region

By default, SecureStash uses the `ap-southeast-1` (Singapore) region. To change this, modify the `AwsClient` constructor in your code:

```java
AwsClient client = new AwsClient(Region.US_EAST_1, "your-bucket-name");
```

### S3 Bucket

The default bucket name is `default-aws-backup`. To use a custom bucket name:

```java
AwsClient client = new AwsClient("my-custom-bucket-name");
```

### Ignored Files

By default, the following files are ignored:
- `history.db` (the SQLite tracking database)
- `.DS_Store` (macOS system files)

To add more ignored patterns, modify the `App` constructor:

```java
ignored.add(".git");
ignored.add("*.tmp");
```

## Usage

### Basic Usage

To back up a directory to S3:

```bash
./gradlew run --args="/path/to/your/directory"
```

Or using the compiled application:

```bash
cd app/build/distributions
unzip app.zip
./app/bin/app /path/to/your/directory
```

### How It Works

1. **Scan**: SecureStash recursively scans the specified directory
2. **Track**: Creates a `history.db` SQLite database in the target directory
3. **Check**: Compares MD5 checksums to detect new or modified files
4. **Upload**: Uploads only new or changed files to S3
5. **Update**: Marks files as uploaded in the database

### Example

```bash
# Back up your documents folder
./gradlew run --args="/home/user/Documents"

# Back up a project directory
./gradlew run --args="/home/user/projects/my-app"
```

## Architecture

### Components

#### 1. **App.java**
Main application entry point that orchestrates the backup process.

**Key Methods:**
- `getFiles(String dir, String base_path)`: Recursively scans directories
- `uploadFiles(ArrayList<FileDetails>, Database)`: Manages the upload workflow
- `main(String[] args)`: Entry point with input validation

#### 2. **AwsClient.java**
Handles all AWS S3 operations using the AWS SDK v2.

**Key Features:**
- Asynchronous S3 client for better performance
- Automatic bucket creation if it doesn't exist
- S3 Transfer Manager for efficient uploads
- Configurable regions and bucket names

#### 3. **Database.java**
Manages SQLite database operations for tracking uploaded files.

**Key Methods:**
- `insertFile(FileDetails)`: Adds file records to the database
- `checkUpload(FileDetails)`: Determines if a file needs uploading
- `uploadFile(FileDetails)`: Marks files as uploaded
- `getAllFiles()`: Retrieves all tracked files

#### 4. **FileDetails.java**
Encapsulates file metadata and operations.

**Key Features:**
- Path management (absolute, relative, base paths)
- MD5 checksum calculation
- File object access

#### 5. **DataBaseObject.java**
Immutable data transfer object for database records.

### Database Schema

```sql
CREATE TABLE IF NOT EXISTS details (
    name text NOT NULL PRIMARY KEY,  -- Relative file path
    tier text,                        -- Storage tier (reserved for future use)
    uploaded boolean,                 -- Upload status
    md5_checksum text                 -- MD5 hash of file contents
);
```

## API Documentation

### App Class

```java
public ArrayList<FileDetails> getFiles(String dir, String base_path)
```
Recursively retrieves all files from a directory.

```java
public void uploadFiles(ArrayList<FileDetails> files, Database db)
```
Uploads files to S3 and updates the database.

### AwsClient Class

```java
public AwsClient()
public AwsClient(String bucket_name)
public AwsClient(Region region)
public AwsClient(Region region, String bucket_name)
```
Constructors with different configuration options.

```java
public CompletedFileUpload uploadFile(FileDetails file)
```
Uploads a single file to S3.

```java
public List<Bucket> listBuckets()
```
Lists all S3 buckets in the configured region.

### Database Class

```java
public void insertFile(FileDetails file)
```
Inserts a new file record into the database.

```java
public boolean checkUpload(FileDetails file)
```
Returns true if the file needs to be uploaded, false if it's already uploaded with matching checksum.

```java
public void uploadFile(FileDetails file)
```
Marks a file as uploaded in the database.

```java
public ArrayList<DataBaseObject> getAllFiles()
```
Retrieves all file records from the database.

### FileDetails Class

```java
public String getChecksum() throws IOException, NoSuchAlgorithmException
```
Calculates and returns the MD5 checksum of the file.

```java
public File getAsFile()
```
Returns a File object for this file.

## Security

### Security Features

- ✅ **SQL Injection Prevention**: Uses prepared statements for all database operations
- ✅ **Resource Management**: Proper try-with-resources to prevent resource leaks
- ✅ **Input Validation**: Validates command-line arguments and file paths
- ✅ **Secure AWS Communication**: Uses AWS SDK with built-in security features
- ✅ **Checksum Verification**: MD5 checksums to detect file tampering

### Security Considerations

1. **AWS Credentials**: Never commit AWS credentials to version control
2. **Bucket Permissions**: Configure appropriate S3 bucket policies
3. **Data Encryption**: Consider enabling S3 server-side encryption
4. **Network Security**: Use VPC endpoints for S3 access in production

### Known Limitations

- MD5 is used for checksums (not collision-resistant for cryptographic purposes)
- Local database is not encrypted
- No built-in file encryption before upload

## Testing

SecureStash includes comprehensive unit tests covering all major components.

### Run All Tests

```bash
./gradlew test
```

### Run Specific Test Class

```bash
./gradlew test --tests org.example.FileDetailsTest
```

### Test Coverage

- **FileDetailsTest**: 8 tests covering file metadata and checksum calculation
- **DatabaseTest**: 13 tests covering database operations and SQL queries
- **DataBaseObjectTest**: 6 tests covering data transfer objects
- **AppMainTest**: 9 tests covering file scanning and directory traversal

Total: **36 unit tests**

### View Test Reports

After running tests, view the HTML report:

```bash
open app/build/reports/tests/test/index.html
```

## Performance Considerations

- **Large Files**: Uses streaming for checksum calculation (1KB buffer)
- **Concurrent Uploads**: AWS Transfer Manager handles parallel uploads
- **Database Indexing**: File paths are indexed as primary keys
- **Memory Efficient**: Processes files one at a time

## Troubleshooting

### Common Issues

**Issue: "No directory path provided"**
```
Solution: Ensure you're passing a directory path as an argument
./gradlew run --args="/path/to/directory"
```

**Issue: AWS credentials not found**
```
Solution: Configure AWS credentials using aws configure or environment variables
```

**Issue: Bucket creation fails**
```
Solution: Check IAM permissions or use an existing bucket
```

**Issue: Database locked**
```
Solution: Ensure no other process is accessing history.db
```

## Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Write tests for new functionality
4. Ensure all tests pass: `./gradlew test`
5. Add JavaDoc comments for public APIs
6. Submit a pull request

### Code Style

- Follow Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for all public classes and methods
- Keep methods focused and concise

## License

This project is licensed under the terms included in the LICENSE file.

## Changelog

### Version 1.0 (Current)

- Initial release with core backup functionality
- SQLite database tracking
- AWS S3 integration
- MD5 checksum verification
- Comprehensive test suite
- Full JavaDoc documentation

## Roadmap

Future enhancements being considered:

- [ ] SHA-256 checksums as an option
- [ ] Encryption before upload
- [ ] Progress bars for large uploads
- [ ] Restore/download functionality
- [ ] Incremental backup strategies
- [ ] Configuration file support
- [ ] Multi-region support
- [ ] Backup scheduling
- [ ] Email notifications

## Support

For issues, questions, or contributions, please:

- Open an issue on GitHub
- Check existing issues for solutions
- Review the documentation

---

**SecureStash** - Secure, Smart, Simple File Backups to AWS S3