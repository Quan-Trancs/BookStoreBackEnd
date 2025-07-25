# 📊 BookStore Backend - Process Structure & Architecture

## 🏗️ **System Architecture Overview**

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           BOOKSTORE BACKEND SYSTEM                              │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐            │
│  │   CLIENT/FE     │    │   LOAD BALANCER │    │   REVERSE PROXY │            │
│  │   (Frontend)    │───▶│   (Optional)    │───▶│   (Nginx)       │            │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘            │
│                                    │                                           │
│                                    ▼                                           │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │                        SPRING BOOT APPLICATION                             │ │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │ │
│  │  │   CONTROLLERS   │  │     SERVICES    │  │   REPOSITORIES  │            │ │
│  │  │                 │  │                 │  │                 │            │ │
│  │  │ • BookController│  │ • BookService   │  │ • BookRepository│            │ │
│  │  │ • UserController│  │ • UserService   │  │ • UserRepository│            │ │
│  │  │ • AuthController│  │ • TaskService   │  │ • AuthorRepo    │            │ │
│  │  │ • AsyncController│ │ • AsyncTaskSvc  │  │ • PublisherRepo │            │ │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘            │ │
│  │           │                     │                     │                    │ │
│  │           ▼                     ▼                     ▼                    │ │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │ │
│  │  │   VALIDATION    │  │   BUSINESS LOGIC│  │   DATA ACCESS   │            │ │
│  │  │                 │  │                 │  │                 │            │ │
│  │  │ • Bean Validation│ │ • BookBusiness  │  │ • JPA/Hibernate │            │ │
│  │  │ • Custom Validators│ • UserBusiness  │  │ • Query Optimization│        │ │
│  │  │ • DTO Validation│ │ • Task Processing│  │ • Connection Pool│            │ │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘            │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
│                                    │                                           │
│                                    ▼                                           │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │                           ASYNC PROCESSING LAYER                           │ │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │ │
│  │  │ WORK ACCEPTOR   │  │ BACKGROUND WORKER│  │   TASK SERVICE  │            │ │
│  │  │                 │  │                 │  │                 │            │ │
│  │  │ • Auth Validation│ │ • Task Queue    │  │ • Task Execution│            │ │
│  │  │ • Request Filter│ │ • Worker Threads │  │ • Progress Track│            │ │
│  │  │ • Rate Limiting │ │ • Status Updates │  │ • Error Handling│            │ │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘            │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
│                                    │                                           │
│                                    ▼                                           │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │                           CACHING LAYER                                    │ │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │ │
│  │  │   L1 CACHE      │  │   L2 CACHE      │  │   CACHE MANAGER │            │ │
│  │  │                 │  │                 │  │                 │            │ │
│  │  │ • Caffeine      │  │ • Redis         │  │ • CompositeCache│            │ │
│  │  │ • In-Memory     │  │ • Distributed   │  │ • TTL Management│            │ │
│  │  │ • Fast Access   │  │ • Persistence   │  │ • Cache Eviction│            │ │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘            │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
│                                    │                                           │
│                                    ▼                                           │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐            │
│  │   POSTGRESQL    │    │     REDIS       │    │   FILE SYSTEM   │            │
│  │   DATABASE      │    │   (Optional)    │    │   (Logs/Uploads)│            │
│  │                 │    │                 │    │                 │            │
│  │ • Book Data     │    │ • Session Store │    │ • Application   │            │
│  │ • User Data     │    │ • Cache Data    │    │   Logs          │            │
│  │ • Audit Data    │    │ • Task Queue    │    │ • File Uploads  │            │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘            │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## 🔄 **Request Flow Process**

### **1. Synchronous Request Flow**
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   CLIENT    │───▶│ CONTROLLER  │───▶│   SERVICE   │───▶│ REPOSITORY  │
│  (Frontend) │    │             │    │             │    │             │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
       ▲                   │                   │                   │
       │                   ▼                   ▼                   ▼
       │            ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
       │            │ VALIDATION  │    │ BUSINESS    │    │   DATABASE  │
       │            │             │    │   LOGIC     │    │             │
       │            └─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │                   │
       └───────────────────┼───────────────────┼───────────────────┘
                           ▼                   ▼                   ▼
                    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
                    │   CACHE     │    │   CACHE     │    │   CACHE     │
                    │   (L1/L2)   │    │   (L1/L2)   │    │   (L1/L2)   │
                    └─────────────┘    └─────────────┘    └─────────────┘
```

### **2. Asynchronous Request Flow**
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   CLIENT    │───▶│ CONTROLLER  │───▶│ WORK ACCEPTOR│───▶│ TASK QUEUE  │
│  (Frontend) │    │             │    │             │    │             │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
       ▲                   │                   │                   │
       │                   ▼                   ▼                   ▼
       │            ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
       │            │ TASK STATUS │    │ AUTH CHECK  │    │ BACKGROUND  │
       │            │   RESPONSE  │    │             │    │   WORKER    │
       │            └─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │                   │
       └───────────────────┼───────────────────┼───────────────────┘
                           ▼                   ▼                   ▼
                    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
                    │ TASK SERVICE│    │ PROGRESS    │    │   DATABASE  │
                    │             │    │  UPDATES    │    │   UPDATE    │
                    └─────────────┘    └─────────────┘    └─────────────┘
```

## 📋 **Detailed Process Breakdown**

### **A. Authentication & Authorization Flow**
```
1. User Login Request
   ├── UserController.login()
   ├── UserService.login()
   ├── UserBusiness.login()
   ├── UserRepository.findByUserName()
   ├── PasswordUtil.verifyPassword()
   └── Return: User Key (Session Token)

2. Request Authorization
   ├── AsyncProcessingWorkAcceptor.acceptWork()
   ├── Validate UserModel (userName, key)
   ├── UserRepository.findByUserName()
   ├── Verify key matches stored key
   └── Return: Authorization Status [200/400/401]
```

### **B. Book Management Flow**
```
1. Book CRUD Operations
   ├── BookController (createBook, updateBook, deleteBook, getBook)
   ├── BookService (createBook, updateBook, deleteBook, findBookById)
   ├── BookBusiness (business logic validation)
   ├── BookRepository (database operations)
   ├── BookEntity (JPA entity)
   └── Return: BookResponseDto or Paginate<BookResponseDto>

2. Book Search & Filtering
   ├── BookController.findBooks()
   ├── BookService.findBooks()
   ├── BookRepository.findBooksWithSearch()
   ├── Query Optimization (indexes, projections)
   ├── Caching (L1: Caffeine, L2: Redis)
   └── Return: Paginated results
```

### **C. Async Task Processing Flow**
```
1. Task Submission
   ├── BookController.asyncUpdateBook()
   ├── AsyncTaskService.submitTask()
   ├── AsyncProcessingWorkAcceptor.acceptWork()
   ├── AsyncProcessingBackgroundWorker.addToRequestQueue()
   └── Return: Task ID for status tracking

2. Task Execution
   ├── Background Worker Thread
   ├── TaskService.runTask()
   ├── AsyncTaskService.updateTaskStatus()
   ├── Complex Processing Steps:
   │   ├── Inventory Check (10%)
   │   ├── Dynamic Pricing (30%)
   │   ├── Book Update (60%)
   │   └── Notifications (90%)
   └── Final Status Update (100%)

3. Task Status Monitoring
   ├── AsyncTaskController.getTaskStatus()
   ├── AsyncTaskService.getTaskStatus()
   ├── In-memory task store (ConcurrentHashMap)
   └── Return: Task status and progress
```

### **D. File Processing Flow**
```
1. CSV Upload
   ├── BookController.uploadBooks()
   ├── BookService.processBookUpload()
   ├── CSV parsing and validation
   ├── Batch database operations
   └── Return: Upload status

2. CSV Download
   ├── BookController.downloadBooks()
   ├── BookService.downloadBooks()
   ├── Database query for all books
   ├── CSV generation
   └── Return: CSV file response
```

## 🔧 **Configuration & Infrastructure**

### **A. Application Configuration**
```
1. Database Configuration
   ├── PostgreSQL connection pool (HikariCP)
   ├── JPA/Hibernate settings
   ├── Query optimization
   └── Migration scripts

2. Caching Configuration
   ├── Caffeine (L1 cache)
   ├── Redis (L2 cache)
   ├── Composite cache manager
   └── TTL and eviction policies

3. Async Processing Configuration
   ├── Thread pool settings
   ├── Task queue management
   ├── Worker thread count
   └── Task timeout settings

4. Security Configuration
   ├── CORS settings
   ├── Rate limiting
   ├── Input validation
   └── Password hashing
```

### **B. Monitoring & Health Checks**
```
1. Spring Boot Actuator
   ├── Health endpoints
   ├── Metrics collection
   ├── Application info
   └── Environment info

2. Logging
   ├── Log4j2 configuration
   ├── Structured logging
   ├── Log rotation
   └── Performance monitoring

3. Database Monitoring
   ├── Connection pool metrics
   ├── Query performance
   ├── Index usage stats
   └── Slow query detection
```

## 🚀 **Deployment Architecture**

### **A. Docker Deployment**
```
┌─────────────────────────────────────────────────────────────────┐
│                    DOCKER COMPOSE STACK                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────┐    ┌─────────────────┐                    │
│  │   POSTGRESQL    │    │   REDIS         │                    │
│  │   CONTAINER     │    │   CONTAINER     │                    │
│  │                 │    │                 │                    │
│  │ • Database      │    │ • Cache Store   │                    │
│  │ • Migrations    │    │ • Session Store │                    │
│  │ • Health Checks │    │ • Task Queue    │                    │
│  └─────────────────┘    └─────────────────┘                    │
│           │                       │                            │
│           └───────────────────────┼────────────────────────────┘
│                                   │                            │
│                                   ▼                            │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │              BOOKSTORE BACKEND CONTAINER                   │ │
│  │                                                             │ │
│  │  ┌─────────────────┐  ┌─────────────────┐                  │ │
│  │  │   SPRING BOOT   │  │   JVM OPTIONS   │                  │ │
│  │  │   APPLICATION   │  │                 │                  │ │
│  │  │                 │  │ • Memory tuning │                  │ │
│  │  │ • REST API      │  │ • GC settings   │                  │ │
│  │  │ • Async Workers │  │ • Performance   │                  │ │
│  │  │ • Health Checks │  │   optimization  │                  │ │
│  │  └─────────────────┘  └─────────────────┘                  │ │
│  └─────────────────────────────────────────────────────────────┘ │
│                                   │                            │
│                                   ▼                            │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │              NGINX REVERSE PROXY (Optional)                │ │
│  │                                                             │ │
│  │ • Load balancing                                            │ │
│  │ • SSL termination                                           │ │
│  │ • Rate limiting                                             │ │
│  │ • Static file serving                                       │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### **B. Production Considerations**
```
1. Scalability
   ├── Horizontal scaling (multiple instances)
   ├── Database clustering
   ├── Redis clustering
   └── Load balancer configuration

2. Security
   ├── SSL/TLS encryption
   ├── API key management
   ├── Input sanitization
   └── Rate limiting

3. Monitoring
   ├── Application metrics
   ├── Database performance
   ├── Error tracking
   └── Alerting systems

4. Backup & Recovery
   ├── Database backups
   ├── Configuration backups
   ├── Disaster recovery plan
   └── Data retention policies
```

## 📊 **Performance Optimization**

### **A. Database Optimization**
```
1. Indexing Strategy
   ├── Primary key indexes
   ├── Foreign key indexes
   ├── Composite indexes
   ├── Full-text search indexes
   └── Partial indexes

2. Query Optimization
   ├── JOIN FETCH for N+1 problem
   ├── Query projections
   ├── Batch operations
   └── Cursor-based pagination

3. Connection Pooling
   ├── HikariCP configuration
   ├── Pool size optimization
   ├── Connection timeout settings
   └── Leak detection
```

### **B. Caching Strategy**
```
1. L1 Cache (Caffeine)
   ├── In-memory caching
   ├── Fast access patterns
   ├── Eviction policies
   └── Size limits

2. L2 Cache (Redis)
   ├── Distributed caching
   ├── Session storage
   ├── Task queue
   └── Cross-instance sharing

3. Cache Invalidation
   ├── TTL-based expiration
   ├── Event-driven invalidation
   ├── Manual cache clearing
   └── Cache warming strategies
```

This comprehensive process structure shows how the BookStore backend system handles both synchronous and asynchronous operations, with proper separation of concerns, caching strategies, and production-ready deployment architecture. 