# Naming Conventions Standardization Plan

## Overview
This document outlines the plan to standardize naming conventions across the BookStoreBackEnd project to improve code consistency, readability, and maintainability.

---

## Current Naming Issues Identified

### **1. Inconsistent Method Naming Patterns**

#### **CRUD Operations**
- **Current**: Mixed patterns
  - `addBook()`, `delBook()`, `updateBook()` (BookService)
  - `create()`, `update()`, `delete()` (BaseService)
  - `createAuthor()`, `updateAuthor()`, `deleteAuthor()` (AuthorService)
- **Issue**: Inconsistent verb usage across services

#### **Query Methods**
- **Current**: Mixed patterns
  - `getBook()`, `getBookById()`, `getBookByIsbn()` (BookService)
  - `getAuthors()`, `getAuthorByName()` (AuthorService)
  - `getPublishers()`, `getPublisherByName()` (PublisherService)
- **Issue**: Inconsistent pluralization and naming

### **2. Variable Naming Inconsistencies**

#### **Parameter Names**
- **Current**: Mixed patterns
  - `addId`, `addName`, `addAuthor`, `addPrice`, `addBookType` (BookController)
  - `updateId`, `updateName`, `updateAuthor`, `updatePrice`, `updateType` (BookController)
  - `delId` (BookController)
- **Issue**: Verb prefixes in parameter names

#### **Search Parameters**
- **Current**: Mixed patterns
  - `searchName`, `searchAuthor`, `searchId`, `searchGenre`, `searchPublisher`
  - `searchTitle`, `searchCountry`, `searchCity`
- **Issue**: Inconsistent search parameter naming

### **3. Class and Package Naming**

#### **Entity vs Model vs DTO**
- **Current**: Mixed patterns
  - `BookEntity`, `BookModel`, `BookDetailDto`, `BookRequestDto`
  - `Author`, `AuthorRequestDto`
  - `Publisher`, `PublisherRequestDto`
- **Issue**: Inconsistent suffix usage

---

## Proposed Naming Standards

### **1. Method Naming Standards**

#### **CRUD Operations**
```java
// Standard CRUD methods (BaseService)
create(T entity)
findById(ID id)
findAll(int page, int size)
update(ID id, T entity)
delete(ID id)

// Specialized CRUD methods
createBook(BookRequestDto request)
findBookById(String id)
findBooksByAuthor(Long authorId)
updateBook(String id, BookRequestDto request)
deleteBook(String id)
```

#### **Query Methods**
```java
// Standard query patterns
findBy[Field](Type value)
findBy[Field1]And[Field2](Type1 value1, Type2 value2)
findBy[Field]OrderBy[Field](Type value)
findAllBy[Condition]()
findTop[Number]By[Field]OrderBy[Field]Desc(int limit)

// Examples
findByName(String name)
findByCountryAndIsActive(String country, Boolean isActive)
findByBirthDateOrderByName(LocalDate birthDate)
findAllByIsActive()
findTop10ByBookCountOrderByBookCountDesc()
```

#### **Business Logic Methods**
```java
// Action-based methods
processBookUpload(MultipartFile file)
validateBookData(BookRequestDto request)
calculateBookPrice(BookEntity book)
reserveBookStock(String bookId, Integer quantity)
releaseBookReservation(String bookId, Integer quantity)
```

### **2. Parameter Naming Standards**

#### **CRUD Parameters**
```java
// Use descriptive names without verb prefixes
createBook(String id, String title, String author, BigDecimal price, String genre)
updateBook(String id, String title, String author, BigDecimal price, String genre)
deleteBook(String id)

// Instead of
addBook(String addId, String addName, String addAuthor, String addPrice, String addBookType)
updateBook(String updateId, String updateName, String updateAuthor, String updatePrice, String updateType)
delBook(String delId)
```

#### **Search Parameters**
```java
// Use consistent search parameter naming
findBooks(String title, String author, String isbn, String genre, String publisher, int page, int size)
findAuthors(String name, String country, Boolean isAlive, int page, int size)
findPublishers(String name, String country, String city, Boolean isActive, int page, int size)

// Instead of mixed patterns
getBook(String searchTitle, String searchAuthor, String searchId, String searchGenre, String searchPublisher, int page, int pageSize)
getAuthors(String searchName, String searchCountry, Boolean isAlive, int page, int pageSize)
```

### **3. Class Naming Standards**

#### **Entity Classes**
```java
// Use entity suffix consistently
BookEntity
AuthorEntity
PublisherEntity
BookTypeEntity
UserEntity
```

#### **DTO Classes**
```java
// Use descriptive DTO suffixes
BookRequestDto      // For create/update requests
BookResponseDto     // For detailed responses
BookListResponseDto // For list responses
BookSummaryDto      // For summary information
```

#### **Model Classes**
```java
// Use model suffix for business models
BookModel
UserModel
OrderModel
```

### **4. Variable Naming Standards**

#### **Local Variables**
```java
// Use camelCase with descriptive names
String bookTitle = book.getTitle();
List<Author> bookAuthors = book.getAuthors();
BigDecimal bookPrice = book.getPrice();
Integer stockQuantity = book.getStockQuantity();

// Avoid abbreviations
String bookId = book.getId();        // Good
String bId = book.getId();           // Bad
```

#### **Method Parameters**
```java
// Use descriptive parameter names
public void createBook(String id, String title, String author, BigDecimal price, String genre)
public void updateBook(String id, String title, String author, BigDecimal price, String genre)
public void deleteBook(String id)

// Avoid verb prefixes
public void addBook(String addId, String addName, String addAuthor, String addPrice, String addBookType) // Bad
```

---

## Implementation Plan

### **Phase 1: Service Layer Standardization**

#### **1.1 BookService Standardization**
```java
// Current
void addBook(BookModel bookModel);
void delBook(String delId);
void updateBook(BookModel bookModel);
Paginate<BookModel> getBook(String searchTitle, String searchAuthor, String searchId, String searchGenre, String searchPublisher, int page, int pageSize);

// Standardized
void createBook(BookRequestDto request);
void deleteBook(String id);
void updateBook(String id, BookRequestDto request);
Paginate<BookResponseDto> findBooks(String title, String author, String isbn, String genre, String publisher, int page, int size);
```

#### **1.2 AuthorService Standardization**
```java
// Current
Author createAuthor(Author author);
Author updateAuthor(Long id, Author author);
void deleteAuthor(Long id);
Paginate<Author> getAuthors(String searchName, String searchCountry, Boolean isAlive, int page, int pageSize);

// Standardized
Author createAuthor(AuthorRequestDto request);
Author updateAuthor(Long id, AuthorRequestDto request);
void deleteAuthor(Long id);
Paginate<Author> findAuthors(String name, String country, Boolean isAlive, int page, int size);
```

#### **1.3 PublisherService Standardization**
```java
// Current
Publisher createPublisher(Publisher publisher);
Publisher updatePublisher(Long id, Publisher publisher);
void deletePublisher(Long id);
Paginate<Publisher> getPublishers(String searchName, String searchCountry, String searchCity, Boolean isActive, int page, int pageSize);

// Standardized
Publisher createPublisher(PublisherRequestDto request);
Publisher updatePublisher(Long id, PublisherRequestDto request);
void deletePublisher(Long id);
Paginate<Publisher> findPublishers(String name, String country, String city, Boolean isActive, int page, int size);
```

### **Phase 2: Controller Layer Standardization**

#### **2.1 BookController Standardization**
```java
// Current
@PostMapping(UrlConstant.ADDBOOK)
public ResponseEntity<String> addBook(@RequestParam String addId, @RequestParam String addName, ...)

@PostMapping(UrlConstant.DELBOOK)
public ResponseEntity<String> delBook(@RequestParam String delId)

@PostMapping(UrlConstant.UPDATEBOOK)
public ResponseEntity<AsyncTaskRequest> updateBook(@RequestParam String updateId, @RequestParam String updateName, ...)

// Standardized
@PostMapping("/books")
public ResponseEntity<BookResponseDto> createBook(@Valid @RequestBody BookRequestDto request)

@DeleteMapping("/books/{id}")
public ResponseEntity<Void> deleteBook(@PathVariable String id)

@PutMapping("/books/{id}")
public ResponseEntity<BookResponseDto> updateBook(@PathVariable String id, @Valid @RequestBody BookRequestDto request)
```

#### **2.2 URL Constants Standardization**
```java
// Current
public static final String ADDBOOK = "addbook";
public static final String DELBOOK = "delbook";
public static final String UPDATEBOOK = "updatebook";

// Standardized
public static final String BOOKS = "books";
public static final String AUTHORS = "authors";
public static final String PUBLISHERS = "publishers";
```

### **Phase 3: Repository Layer Standardization**

#### **3.1 Repository Method Naming**
```java
// Current mixed patterns
findByNameIgnoreCase(String name)
findByCountryIgnoreCase(String country)
findAuthorsWithSearch(String searchName, String searchCountry, Boolean isAlive, Pageable pageable)

// Standardized
findByNameIgnoreCase(String name)
findByCountryIgnoreCase(String country)
findByNameContainingIgnoreCaseAndCountryContainingIgnoreCaseAndIsAlive(String name, String country, Boolean isAlive, Pageable pageable)
```

### **Phase 4: DTO and Model Standardization**

#### **4.1 Create Request DTOs**
```java
// Standardized request DTOs
BookRequestDto
AuthorRequestDto
PublisherRequestDto
UserRequestDto
```

#### **4.2 Response DTOs**
```java
// Standardized response DTOs
BookResponseDto
BookListResponseDto
BookSummaryDto
AuthorResponseDto
PublisherResponseDto
```

---

## Migration Strategy

### **Step 1: Create New Standardized Interfaces**
1. Create new service interfaces with standardized method names
2. Create new DTO classes with standardized naming
3. Create new controller endpoints with standardized URLs

### **Step 2: Implement New Standardized Classes**
1. Implement new service classes with standardized methods
2. Implement new controller classes with standardized endpoints
3. Create new repository methods with standardized naming

### **Step 3: Gradual Migration**
1. Keep old methods as deprecated for backward compatibility
2. Update existing code to use new standardized methods
3. Remove deprecated methods after migration period

### **Step 4: Update Documentation**
1. Update API documentation with new standardized endpoints
2. Update README with new naming conventions
3. Create migration guide for developers

---

## Benefits of Standardization

### **1. Improved Readability**
- Consistent method names make code easier to understand
- Clear separation between different types of operations
- Better API discoverability

### **2. Enhanced Maintainability**
- Standardized patterns reduce cognitive load
- Easier to add new features following established patterns
- Reduced chance of naming conflicts

### **3. Better Developer Experience**
- Intuitive method names improve productivity
- Consistent patterns across all services
- Clear API contracts

### **4. Scalability**
- Standardized patterns scale better as codebase grows
- Easier to onboard new developers
- Consistent patterns for new services

---

## Success Metrics

### **Code Quality Metrics**
- **Consistency**: 100% adherence to naming standards
- **Readability**: Improved code review feedback
- **Maintainability**: Reduced time for common changes

### **Developer Experience Metrics**
- **Productivity**: Faster development of new features
- **Onboarding**: Reduced time for new developers
- **Documentation**: Clearer API documentation

---

## Conclusion

Standardizing naming conventions will significantly improve code quality, maintainability, and developer experience. The phased approach ensures minimal disruption while delivering maximum benefit.

**Key Principles:**
- ✅ **Consistency**: Use the same patterns across all components
- ✅ **Clarity**: Choose descriptive, unambiguous names
- ✅ **Simplicity**: Avoid unnecessary complexity in naming
- ✅ **Standards**: Follow Java and Spring Boot conventions 