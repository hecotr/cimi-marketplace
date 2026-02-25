# AI Marketplace Deployment Guide

## Prerequisites

- Docker and Docker Compose
- Java 17
- Node.js 18+
- PostgreSQL 13+
- MinIO (for object storage)

## Quick Start with Docker Compose

```bash
# Clone the repository
git clone <repository-url>
cd ai-marketplace

# Start all services
docker-compose up -d

# Initialize database
docker-compose exec backend psql -U postgres -d ai_marketplace
docker-compose exec backend psql -U postgres ai_marketplace -f /docker-entrypoint-initdb.d/schema.sql
docker-compose exec backend psql -U postgres ai_marketplace -f /docker-entrypoint-initdb.d/enhanced-schema.sql
```

## Backend Deployment

### Build and Run

```bash
cd backend
mvn clean package -DskipTests
java -jar target/ai-marketplace-backend-1.0.0.jar
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| DB_HOST | PostgreSQL host | localhost |
| DB_PORT | PostgreSQL port | 5432 |
| DB_NAME | Database name | ai_marketplace |
| DB_USER | Database user | postgres |
| DB_PASSWORD | Database password | postgres |
| SERVER_PORT | Server port | 8080 |
| MINIO_ENDPOINT | MinIO endpoint | http://localhost:9000 |
| MINIO_ACCESS_KEY | MinIO access key | minioadmin |
| MINIO_SECRET_KEY | MinIO secret key | minioadmin |
| MINIO_BUCKET | MinIO bucket name | ai-marketplace |
| JWT_SECRET | JWT signing secret | (change in production) |
| JWT_EXPIRATION | JWT token expiration (ms) | 86400000 |

## Frontend Deployment

### Build and Run

```bash
cd frontend
npm install
npm run build
```

The built files will be in `frontend/dist/`.

### Environment Variables

Create `.env.production`:

```
VITE_API_BASE_URL=https://your-api-domain.com/api
```

## Production Deployment

### Nginx Configuration

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # Frontend
    location / {
        root /var/www/ai-marketplace/dist;
        try_files $uri $uri/ /index.html;
    }

    # Backend API
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### Database Migration

```bash
# Apply schema
psql -U postgres -h localhost -d ai_marketplace -f backend/src/main/resources/db/schema.sql

# Apply enhanced schema
psql -U postgres -h localhost -d ai_marketplace -f backend/src/main/resources/db/enhanced-schema.sql
```

## Security Notes

1. **Change all default passwords** in production
2. **Use strong JWT secret** at least 256 bits
3. **Enable HTTPS** in production
4. **Configure CORS** properly for your domain
5. **Use environment variables** for sensitive data

## Monitoring

- Backend logs: `docker-compose logs backend`
- Frontend logs: Browser console
- MinIO console: http://localhost:9001 (default credentials: minioadmin/minioadmin)

## Troubleshooting

### Backend won't start
- Check database connection
- Verify port availability
- Check logs: `docker-compose logs backend`

### Frontend build fails
- Clear `node_modules`: `rm -rf node_modules && npm install`
- Check Node.js version (18+ required)

### File upload fails
- Verify MinIO is running
- Check bucket exists
- Verify MinIO credentials
