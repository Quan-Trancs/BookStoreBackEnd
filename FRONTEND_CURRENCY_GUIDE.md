# Frontend Currency Conversion Guide

## 🎯 Overview

The backend now stores all prices in **USD** and delegates currency conversion to the frontend. This approach provides better user experience and reduces backend complexity.

## 📊 Backend Changes

### **Simplified Data Model**
- ✅ All prices stored in USD
- ✅ No currency conversion logic in backend
- ✅ Simple price format: `"25.50 USD"`

### **API Response Format**
```json
{
  "id": "BOOK001",
  "name": "Sample Book",
  "author": "John Doe",
  "price": "25.50 USD",
  "bookType": "Fiction"
}
```

## 🌐 Frontend Implementation Options

### **1. Exchange Rate APIs (Recommended)**

#### **Fixer.io (Free Tier)**
```javascript
// Get real-time exchange rates
const API_KEY = 'your_api_key';
const response = await fetch(`http://data.fixer.io/api/latest?access_key=${API_KEY}&base=USD&symbols=VND,EUR`);
const rates = await response.json();

// Convert USD to VND
const usdPrice = 25.50;
const vndPrice = usdPrice * rates.rates.VND;
```

#### **ExchangeRate-API (Free)**
```javascript
// Simple API without authentication
const response = await fetch('https://api.exchangerate-api.com/v4/latest/USD');
const data = await response.json();

// Convert USD to VND
const usdPrice = 25.50;
const vndPrice = usdPrice * data.rates.VND;
```

#### **Open Exchange Rates**
```javascript
// More comprehensive API
const APP_ID = 'your_app_id';
const response = await fetch(`https://openexchangerates.org/api/latest.json?app_id=${APP_ID}`);
const rates = await response.json();

// Convert USD to VND
const usdPrice = 25.50;
const vndPrice = usdPrice * rates.rates.VND;
```

### **2. Currency Conversion Service**

#### **Create a Currency Service**
```javascript
class CurrencyService {
  constructor() {
    this.baseCurrency = 'USD';
    this.rates = {};
    this.lastUpdate = null;
  }

  async updateRates() {
    try {
      const response = await fetch('https://api.exchangerate-api.com/v4/latest/USD');
      const data = await response.json();
      this.rates = data.rates;
      this.lastUpdate = new Date();
    } catch (error) {
      console.error('Failed to update exchange rates:', error);
      // Use fallback rates
      this.rates = {
        VND: 23000,
        EUR: 0.85,
        USD: 1
      };
    }
  }

  convert(amount, fromCurrency, toCurrency) {
    if (fromCurrency === toCurrency) return amount;
    
    // Convert to USD first, then to target currency
    const usdAmount = fromCurrency === 'USD' ? amount : amount / this.rates[fromCurrency];
    return toCurrency === 'USD' ? usdAmount : usdAmount * this.rates[toCurrency];
  }

  formatPrice(amount, currency) {
    switch (currency) {
      case 'VND':
        return `${Math.round(amount).toLocaleString()} VND`;
      case 'USD':
        return `$${amount.toFixed(2)}`;
      case 'EUR':
        return `€${amount.toFixed(2)}`;
      default:
        return `${amount.toFixed(2)} ${currency}`;
    }
  }
}
```

### **3. React Component Example**

#### **Currency Converter Component**
```jsx
import React, { useState, useEffect } from 'react';

const CurrencyConverter = ({ usdPrice }) => {
  const [rates, setRates] = useState({});
  const [selectedCurrency, setSelectedCurrency] = useState('USD');
  const [convertedPrice, setConvertedPrice] = useState(usdPrice);

  useEffect(() => {
    fetchRates();
  }, []);

  useEffect(() => {
    if (rates[selectedCurrency]) {
      const converted = usdPrice * rates[selectedCurrency];
      setConvertedPrice(converted);
    }
  }, [usdPrice, selectedCurrency, rates]);

  const fetchRates = async () => {
    try {
      const response = await fetch('https://api.exchangerate-api.com/v4/latest/USD');
      const data = await response.json();
      setRates(data.rates);
    } catch (error) {
      console.error('Failed to fetch rates:', error);
      // Fallback rates
      setRates({
        VND: 23000,
        EUR: 0.85,
        USD: 1
      });
    }
  };

  const formatPrice = (amount, currency) => {
    switch (currency) {
      case 'VND':
        return `${Math.round(amount).toLocaleString()} VND`;
      case 'USD':
        return `$${amount.toFixed(2)}`;
      case 'EUR':
        return `€${amount.toFixed(2)}`;
      default:
        return `${amount.toFixed(2)} ${currency}`;
    }
  };

  return (
    <div className="currency-converter">
      <div className="price-display">
        <span className="price">{formatPrice(convertedPrice, selectedCurrency)}</span>
      </div>
      <div className="currency-selector">
        <select 
          value={selectedCurrency} 
          onChange={(e) => setSelectedCurrency(e.target.value)}
        >
          <option value="USD">USD</option>
          <option value="VND">VND</option>
          <option value="EUR">EUR</option>
        </select>
      </div>
    </div>
  );
};

export default CurrencyConverter;
```

### **4. Vue.js Component Example**

#### **Currency Converter Component**
```vue
<template>
  <div class="currency-converter">
    <div class="price-display">
      <span class="price">{{ formattedPrice }}</span>
    </div>
    <div class="currency-selector">
      <select v-model="selectedCurrency">
        <option value="USD">USD</option>
        <option value="VND">VND</option>
        <option value="EUR">EUR</option>
      </select>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CurrencyConverter',
  props: {
    usdPrice: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      rates: {},
      selectedCurrency: 'USD'
    };
  },
  computed: {
    convertedPrice() {
      if (!this.rates[this.selectedCurrency]) return this.usdPrice;
      return this.usdPrice * this.rates[this.selectedCurrency];
    },
    formattedPrice() {
      return this.formatPrice(this.convertedPrice, this.selectedCurrency);
    }
  },
  async mounted() {
    await this.fetchRates();
  },
  methods: {
    async fetchRates() {
      try {
        const response = await fetch('https://api.exchangerate-api.com/v4/latest/USD');
        const data = await response.json();
        this.rates = data.rates;
      } catch (error) {
        console.error('Failed to fetch rates:', error);
        // Fallback rates
        this.rates = {
          VND: 23000,
          EUR: 0.85,
          USD: 1
        };
      }
    },
    formatPrice(amount, currency) {
      switch (currency) {
        case 'VND':
          return `${Math.round(amount).toLocaleString()} VND`;
        case 'USD':
          return `$${amount.toFixed(2)}`;
        case 'EUR':
          return `€${amount.toFixed(2)}`;
        default:
          return `${amount.toFixed(2)} ${currency}`;
      }
    }
  }
};
</script>
```

## 🔧 Implementation Steps

### **1. Choose Exchange Rate API**
- **Free**: ExchangeRate-API, Fixer.io (limited)
- **Paid**: Open Exchange Rates, CurrencyLayer

### **2. Create Currency Service**
- Handle API calls
- Implement caching
- Add fallback rates
- Error handling

### **3. Update UI Components**
- Add currency selector
- Display converted prices
- Handle loading states
- Show error messages

### **4. Implement Caching**
```javascript
// Cache rates for 1 hour
const CACHE_DURATION = 60 * 60 * 1000; // 1 hour

class CachedCurrencyService {
  constructor() {
    this.cache = new Map();
  }

  async getRates() {
    const cached = this.cache.get('rates');
    if (cached && Date.now() - cached.timestamp < CACHE_DURATION) {
      return cached.data;
    }

    const rates = await this.fetchRates();
    this.cache.set('rates', {
      data: rates,
      timestamp: Date.now()
    });

    return rates;
  }
}
```

## 🎨 UI/UX Considerations

### **1. Currency Selector**
- Dropdown or toggle buttons
- Show current selection
- Update prices instantly

### **2. Price Display**
- Clear formatting
- Currency symbols
- Responsive design

### **3. Loading States**
- Show spinner while fetching rates
- Graceful degradation
- Offline support

### **4. Error Handling**
- Show fallback rates
- User-friendly error messages
- Retry functionality

## 📱 Mobile Considerations

### **1. Responsive Design**
- Touch-friendly currency selector
- Readable price display
- Optimized for small screens

### **2. Performance**
- Lazy load exchange rates
- Cache rates locally
- Minimize API calls

## 🔒 Security Considerations

### **1. API Keys**
- Store keys securely
- Use environment variables
- Rotate keys regularly

### **2. Rate Limiting**
- Implement request throttling
- Use multiple API providers
- Handle rate limit errors

## 📊 Monitoring

### **1. Track Usage**
- Monitor API calls
- Track conversion rates
- Log errors

### **2. Performance Metrics**
- Response times
- Cache hit rates
- User engagement

---

**Benefits of Frontend Currency Handling:**
- ✅ **Better UX** - Instant conversion
- ✅ **Reduced Backend Load** - No conversion processing
- ✅ **Real-time Rates** - Always up-to-date
- ✅ **Offline Support** - Cached rates
- ✅ **Multiple Currencies** - Easy to add new ones
- ✅ **User Preference** - Users choose their currency 