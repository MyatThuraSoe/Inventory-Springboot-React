# MinIO Setup Guide for Local Development

This project now uses **MinIO** as a local, self-hosted alternative to AWS S3 for storing product images.

## What is MinIO?

MinIO is an open-source, S3-compatible object storage server that you can run locally. It provides the same API as AWS S3, so you can test your S3 code without needing an AWS account.

## Prerequisites

- Docker and Docker Compose installed
- Java 21
- MySQL Server running

## Quick Start

### 1. Start MinIO

From the project root directory (`/workspace`), run:

```bash
docker-compose up -d
```

This will:
- Download the MinIO Docker image
- Start MinIO on port 9000 (API) and 9001 (Console UI)
- Create a persistent volume for data storage

### 2. Access MinIO Console

Open your browser and go to:
- **Console UI**: http://localhost:9001
- **Login credentials**: 
  - Username: `minioadmin`
  - Password: `minioadmin`

### 3. Create Bucket

In the MinIO Console:
1. Click "Buckets" in the left sidebar
2. Click "Create Bucket"
3. Enter bucket name: `product-images`
4. Click "Create Bucket"

### 4. Run the Backend

The application is already configured to use MinIO. Just start your Spring Boot backend:

```bash
cd backend
./mvnw spring-boot:run
```

## Configuration

The MinIO settings are in `backend/src/main/resources/application.properties`:

```properties
minio.endpoint=http://localhost:9000
minio.access-key=minioadmin
minio.secret-key=minioadmin
minio.bucket-name=product-images
```

## Stopping MinIO

To stop MinIO:

```bash
docker-compose down
```

To stop and remove all data:

```bash
docker-compose down -v
```

## Benefits of Using MinIO

✅ **Free & Open Source** - No AWS costs during development
✅ **S3 Compatible** - Same API as AWS S3
✅ **Local Development** - Fast, no network latency
✅ **Web Console** - Easy to browse and manage files
✅ **Production Ready** - Can be used in production too
✅ **Docker Based** - Easy to set up and tear down

## Next Steps

The MinIO integration has been implemented! Here's what was done:

1. ✅ AWS S3 dependency uncommented in `pom.xml`
2. ✅ Created `MinioConfig.java` - Spring configuration for MinIO client
3. ✅ Created `MinioStorageService.java` - Service for upload/download operations
4. ✅ Updated `ProductServiceImpl.java` to use MinIO instead of local file storage

Now you just need to:
1. Start MinIO (see Quick Start above)
2. Create the `product-images` bucket in MinIO console
3. Run your Spring Boot backend

## Troubleshooting

**Port already in use?**
- Check if another service is using ports 9000 or 9001
- Change the ports in `docker-compose.yml`

**MinIO not starting?**
- Ensure Docker is running: `docker ps`
- Check logs: `docker-compose logs minio`

**Cannot access console?**
- Wait a few seconds for MinIO to fully start
- Check firewall settings
