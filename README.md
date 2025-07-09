# BookStore Backend API

A Spring Boot REST API for managing a bookstore with user authentication and book management features.

## 🚀 Features

- **Book Management**: CRUD operations for books with search and pagination
- **User Authentication**: Secure login with password hashing
- **File Operations**: Upload/download books as CSV files
- **Async Processing**: Background task processing for book updates
- **Data Validation**: Input validation and error handling
- **CORS Support**: Cross-origin resource sharing enabled

## 🛠️ Technology Stack

- **Java 8**
- **Spring Boot 2.7.4**
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**
- **Gradle**

## 📋 Prerequisites

- Java 8 or higher
- PostgreSQL database
- Gradle (or use the included wrapper)

## 🔧 Setup Instructions

### 1. Database Setup

1. Create a PostgreSQL database
2. Update the database connection in `application.properties` or set environment variables:
   ```bash
   export DB_URL=jdbc:postgresql://localhost:5432/your_database_name
   export DB_USERNAME=your_username
   export DB_PASSWORD=your_secure_password
   ```

### 2. Application Setup

1. Clone the repository
2. Navigate to the project directory
3. Run the application:
   ```bash
   ./gradlew bootRun
   ```

The application will start on `http://localhost:8082/api`

## 🔐 Security Features

### Password Hashing
- Passwords are hashed using SHA-256 with salt
- Salt is generated using SecureRandom
- Password verification is done securely

### Environment Variables
- Database credentials are stored in environment variables
- No hardcoded passwords in the codebase

### Input Validation
- Bean Validation annotations for all inputs
- Custom error handling for validation violations
- Pattern validation for usernames and passwords

## 📚 API Endpoints

### Authentication
- `POST /api/user/login` - User login

### Books
- `GET /api/book/list` - Get books with search and pagination
- `GET /api/book/type` - Get book types
- `POST /api/book/addbook` - Add a new book
- `POST /api/book/delbook` - Delete a book
- `POST /api/book/updatebook` - Update a book (requires authentication)
- `POST /api/book/upload-all` - Upload books from CSV file
- `GET /api/book/download-all` - Download books as CSV file

## 🚨 Security Recommendations

### Before Production Deployment

1. **Enable HTTPS**: Configure SSL/TLS certificates
2. **Add Rate Limiting**: Implement API rate limiting
3. **Add Request Logging**: Log all API requests for monitoring
4. **Database Security**: 
   - Use connection pooling
   - Implement database user with minimal privileges
   - Enable database encryption
5. **Add Monitoring**: Implement health checks and metrics
6. **Backup Strategy**: Implement regular database backups

### Environment Variables Required

```bash
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/bookstore
DB_USERNAME=bookstore_user
DB_PASSWORD=your_secure_password

# Server Configuration (optional)
SERVER_PORT=8082
SERVER_SERVLET_CONTEXT_PATH=/api
```

## 🐛 Recent Fixes

### Critical Security Fixes
- ✅ Removed hardcoded database password
- ✅ Implemented password hashing with salt
- ✅ Fixed string comparison bugs (`==` vs `.equals()`)
- ✅ Fixed setter method parameter assignments

### Code Quality Improvements
- ✅ Improved price parsing with proper validation
- ✅ Enhanced CSV file upload with error handling
- ✅ Fixed login logic to prevent unauthorized saves
- ✅ Added proper shutdown handling for background workers
- ✅ Added comprehensive error handling and logging

## 📝 Usage Examples

### Login
```bash
curl -X POST "http://localhost:8082/api/user/login" \
  -d "userName=your_username" \
  -d "password=your_password" \
  -d "role=customer"
```

### Get Books
```bash
curl "http://localhost:8082/api/book/list?page=0&pageSize=10"
```

### Add Book
```bash
curl -X POST "http://localhost:8082/api/book/addbook" \
  -d "addId=BOOK001" \
  -d "addName=Sample Book" \
  -d "addAuthor=John Doe" \
  -d "addPrice=50000VND" \
  -d "addBookType=Fiction"
```

## 🧪 Testing

Run the test suite:
```bash
./gradlew test
```

## 📊 Database Schema

### Tables
- `book` - Book information
- `bookType` - Book categories
- `user2` - User accounts
- `userRole` - User roles and permissions

### Relationships
- Books belong to a BookType (Many-to-One)
- Users belong to a UserRole (Many-to-One)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For issues and questions, please create an issue in the repository. 