# MinIO Integration - Complete Setup Guide

## Overview
The application has been successfully integrated with MinIO for product image storage. This replaces the previous local file system approach with a production-ready, S3-compatible object storage solution.

## What Was Implemented

### 1. **Dependencies** (`pom.xml`)
- AWS S3 SDK dependency enabled (works with MinIO)
- Version: `2.29.6`

### 2. **Configuration Classes**

#### `MinioConfig.java`
- Spring configuration class for MinIO client
- Creates and configures the `S3Client` bean
- Reads configuration from `application.properties`
- Uses path-style access (required for MinIO)

#### `MinioStorageService.java`
- Service layer for all storage operations
- Methods:
  - `uploadFile(MultipartFile)` - Upload images to MinIO
  - `deleteFile(String)` - Remove images from MinIO
  - `getFileUrl(String)` - Generate accessible URLs
  - Auto-creates bucket if it doesn't exist

### 3. **Updated Service Implementation**

#### `ProductServiceImpl.java`
- Injected `MinioStorageService` dependency
- Updated `saveProduct()` to use MinIO upload
- Updated `updateProduct()` to use MinIO upload
- Removed old local file storage methods (`saveImage()`, `saveImage2()`)
- Image URLs now point to MinIO endpoint

### 4. **Configuration** (`application.properties`)
```properties
minio.endpoint=http://localhost:9000
minio.access-key=minioadmin
minio.secret-key=minioadmin
minio.bucket-name=product-images
```

### 5. **Docker Compose** (`docker-compose.yml`)
- MinIO container configuration
- Ports: 9000 (API), 9001 (Console)
- Persistent volume for data
- Health checks configured

## How to Run

### Step 1: Start MinIO
```bash
cd /workspace
docker-compose up -d
```

### Step 2: Create Bucket
1. Open browser: http://localhost:9001
2. Login with:
   - Username: `minioadmin`
   - Password: `minioadmin`
3. Click "Buckets" → "Create Bucket"
4. Enter name: `product-images`
5. Click "Create Bucket"

### Step 3: Run Backend
```bash
cd /workspace/backend
./mvnw spring-boot:run
```

The backend will start on http://localhost:5050

### Step 4: Test Image Upload
When you create or update a product with an image:
1. Image is uploaded to MinIO bucket
2. URL format: `http://localhost:9000/product-images/{filename}`
3. URL is saved in database

## File Structure
```
backend/src/main/java/com/mdevm/InventoryMgtSystem/
├── config/
│   └── MinioConfig.java          # MinIO client configuration
├── services/
│   ├── storage/
│   │   └── MinioStorageService.java  # Storage operations
│   └── impl/
│       └── ProductServiceImpl.java   # Updated to use MinIO
```

## Testing the Integration

### Via API
```bash
# Create a product with image
curl -X POST http://localhost:5050/api/products \
  -H "Content-Type: multipart/form-data" \
  -F "name=Test Product" \
  -F "sku=TEST-001" \
  -F "price=99.99" \
  -F "stockQuantity=10" \
  -F "description=Test description" \
  -F "categoryId=1" \
  -F "imageFile=@/path/to/image.jpg"
```

### Via MinIO Console
1. Go to http://localhost:9001
2. Navigate to `product-images` bucket
3. View uploaded files
4. Download or preview images

## Benefits

✅ **Production Ready** - Same code works with AWS S3 in production
✅ **Scalable** - Object storage scales automatically
✅ **Cost Effective** - Free for local development
✅ **Easy Management** - Web console for browsing files
✅ **Durable** - Data persisted in Docker volume
✅ **S3 Compatible** - Switch to AWS S3 by changing endpoint

## Migration to Production (AWS S3)

To use AWS S3 in production, only change `application.properties`:

```properties
# Production AWS S3 Configuration
minio.endpoint=https://s3.us-east-1.amazonaws.com
minio.access-key=YOUR_AWS_ACCESS_KEY
minio.secret-key=YOUR_AWS_SECRET_KEY
minio.bucket-name=your-production-bucket
```

No code changes needed!

## Troubleshooting

### MinIO won't start
```bash
# Check if ports are in use
netstat -tlnp | grep 9000
netstat -tlnp | grep 9001

# Stop conflicting services or change ports in docker-compose.yml
```

### Bucket not found errors
1. Ensure MinIO is running: `docker-compose ps`
2. Manually create bucket in console
3. Check bucket name matches in `application.properties`

### Upload fails
1. Check MinIO logs: `docker-compose logs minio`
2. Verify credentials in `application.properties`
3. Ensure bucket exists and is accessible
4. Check file size limits in `application.properties`

### Cannot access uploaded images
1. Verify MinIO endpoint URL is correct
2. Check bucket policy (should be public for direct access)
3. For production, consider using CloudFront or presigned URLs

## Stopping MinIO

```bash
# Stop without removing data
docker-compose down

# Stop and remove all data (including uploaded images)
docker-compose down -v
```

## Security Notes

⚠️ **For Development Only**: Current setup uses default credentials (`minioadmin/minioadmin`)

**For Production:**
1. Change default credentials
2. Use environment variables for sensitive data
3. Configure proper bucket policies
4. Enable HTTPS
5. Use IAM roles or presigned URLs for access control

## Additional Resources

- [MinIO Documentation](https://min.io/docs/minio/linux/index.html)
- [AWS S3 SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html)
- [Docker Compose Reference](https://docs.docker.com/compose/)
