# Currency Implementation with Java Money API

## 🏦 Overview

This implementation uses the **Java Money API (JSR 354)** to handle currency conversions, providing a robust, type-safe, and standardized approach to monetary operations.

## 📦 Dependencies Added

```gradle
// Java Money API (JSR 354)
implementation 'org.javamoney:moneta:1.4.2'
implementation 'org.zalando:jackson-datatype-money:1.3.0'
```

## 🔧 Key Components

### 1. CurrencyService Interface
- **Location**: `src/main/java/quantran/api/service/CurrencyService.java`
- **Purpose**: Defines currency operations contract
- **Methods**:
  - `convert()` - Currency conversion
  - `parsePrice()` - Parse price strings
  - `formatPrice()` - Format monetary amounts

### 2. CurrencyServiceImpl Implementation
- **Location**: `src/main/java/quantran/api/service/impl/CurrencyServiceImpl.java`
- **Features**:
  - Real-time exchange rates via ECB (European Central Bank)
  - Fallback to 1:1 conversion if rates unavailable
  - Support for USD, VND, EUR currencies
  - Proper error handling and logging

### 3. MoneyConfig Configuration
- **Location**: `src/main/java/quantran/api/config/MoneyConfig.java`
- **Purpose**: Configure exchange rate providers
- **Providers Available**:
  - `ECB` - European Central Bank (recommended)
  - `IMF` - International Monetary Fund
  - `IDENTITY` - 1:1 conversion (for testing)

## ⚙️ Configuration

### Application Properties
```properties
# Currency Configuration
app.currency.default-currency=USD
app.currency.display-currency=VND
app.currency.exchange-rate-provider=ECB
app.currency.real-time-rates=true

# Fallback rates (when real-time rates unavailable)
app.currency.fallback.usd-to-vnd=23000
app.currency.fallback.eur-to-vnd=25000
app.currency.fallback.eur-to-usd=1.08
```

## 💰 Usage Examples

### 1. Currency Conversion
```java
@Autowired
private CurrencyService currencyService;

// Convert USD to VND
BigDecimal usdAmount = new BigDecimal("25.00");
BigDecimal vndAmount = currencyService.convert(usdAmount, "USD", "VND");
// Result: ~575,000 VND (depending on current rates)
```

### 2. Parse Price Strings
```java
// Parse price with currency
MonetaryAmount amount = currencyService.parsePrice("1000VND");
// Result: MonetaryAmount with 1000 VND

MonetaryAmount usdAmount = currencyService.parsePrice("25.50USD");
// Result: MonetaryAmount with 25.50 USD
```

### 3. Format Prices
```java
MonetaryAmount amount = currencyService.parsePrice("575000VND");
String formatted = currencyService.formatPrice(amount);
// Result: "575000 VND"
```

## 🗄️ Database Changes

### New Currency Column
```sql
-- Added to books table
ALTER TABLE books ADD COLUMN currency VARCHAR(3) NOT NULL DEFAULT 'USD';
CREATE INDEX idx_book_currency ON books(currency);
```

### Migration Script
- **Location**: `src/main/resources/db/migration/V2__Add_currency_column.sql`
- **Purpose**: Add currency column to existing databases

## 🔄 Migration from Old Implementation

### Before (Hardcoded)
```java
// Old hardcoded conversion
return price.divide(BigDecimal.valueOf(23000), 2, RoundingMode.HALF_UP);
```

### After (Configurable)
```java
// New configurable conversion
BigDecimal converted = currencyService.convert(price, "VND", "USD");
```

## 🌐 Exchange Rate Sources

### Real-time Rates (ECB)
- **Source**: European Central Bank
- **Update Frequency**: Daily
- **Coverage**: Major world currencies
- **Reliability**: High

### Fallback Rates
- Used when real-time rates unavailable
- Configurable via properties
- Ensures system continues to function

## 🧪 Testing

### CurrencyTestUtil
- **Location**: `src/main/java/quantran/api/util/CurrencyTestUtil.java`
- **Purpose**: Simple testing without full test framework
- **Tests**:
  - Price parsing
  - Currency conversion
  - Price formatting

### Manual Testing
```bash
# Test with curl
curl -X POST "http://localhost:8082/api/v1/books" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "addId=TEST001&addName=Test Book&addBookType=Fiction&addAuthor=Test Author&addPrice=1000VND"
```

## 🚀 Benefits

### 1. **Standard Compliance**
- Uses official Java standard (JSR 354)
- Industry best practices
- Well-tested and maintained

### 2. **Type Safety**
- `MonetaryAmount` type prevents errors
- Currency validation
- Proper rounding and precision

### 3. **Real-time Rates**
- Live exchange rates from ECB
- Automatic updates
- No manual rate management

### 4. **Flexibility**
- Multiple exchange rate providers
- Configurable fallback rates
- Easy to extend for new currencies

### 5. **Error Handling**
- Graceful fallbacks
- Proper logging
- System resilience

## 🔧 Troubleshooting

### Common Issues

#### 1. Exchange Rate Provider Unavailable
```
Error: Exchange rate provider 'ECB' not available
```
**Solution**: Check internet connection or use fallback provider

#### 2. Currency Not Supported
```
Error: Currency 'XYZ' not supported
```
**Solution**: Add currency to supported list in CurrencyServiceImpl

#### 3. Invalid Price Format
```
Error: Invalid price format: 1000
```
**Solution**: Ensure price ends with currency code (e.g., "1000VND")

### Debug Commands
```bash
# Check application logs for currency operations
tail -f logs/bookstore-backend.log | grep -i currency

# Test currency conversion via API
curl "http://localhost:8082/api/v1/books?searchName=test"
```

## 📈 Future Enhancements

### 1. Additional Currencies
- Add more currency support
- Configure new exchange rate providers

### 2. Caching
- Cache exchange rates
- Reduce API calls
- Improve performance

### 3. Historical Rates
- Store historical exchange rates
- Support for date-based conversions

### 4. API Endpoints
- Currency conversion endpoint
- Available currencies endpoint
- Exchange rate status endpoint

---

**Note**: This implementation provides a production-ready, scalable solution for currency handling that follows industry standards and best practices. 