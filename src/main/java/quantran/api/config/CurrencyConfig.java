package quantran.api.config;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app.currency")
@Data
@Log4j2
public class CurrencyConfig {
    
    private String defaultCurrency = "USD";
    private String displayCurrency = "VND";
    private Map<String, BigDecimal> exchangeRates = new HashMap<>();
    
    // Configurable exchange rates via properties (kebab-case for properties)
    private BigDecimal usdToVndRate = BigDecimal.valueOf(23000);
    private BigDecimal eurToVndRate = BigDecimal.valueOf(25000);
    private BigDecimal eurToUsdRate = BigDecimal.valueOf(1.08);
    
    public CurrencyConfig() {
        initializeExchangeRates();
    }
    
    private void initializeExchangeRates() {
        // USD conversions
        exchangeRates.put("USD_TO_VND", usdToVndRate);
        exchangeRates.put("VND_TO_USD", BigDecimal.ONE.divide(usdToVndRate, 6, RoundingMode.HALF_UP));
        
        // EUR conversions
        exchangeRates.put("EUR_TO_VND", eurToVndRate);
        exchangeRates.put("VND_TO_EUR", BigDecimal.ONE.divide(eurToVndRate, 6, RoundingMode.HALF_UP));
        exchangeRates.put("EUR_TO_USD", eurToUsdRate);
        exchangeRates.put("USD_TO_EUR", BigDecimal.ONE.divide(eurToUsdRate, 6, RoundingMode.HALF_UP));
        
        log.info("Currency configuration initialized with rates: USD_TO_VND={}, EUR_TO_VND={}, EUR_TO_USD={}", 
                usdToVndRate, eurToVndRate, eurToUsdRate);
    }
    
    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        
        String rateKey = fromCurrency + "_TO_" + toCurrency;
        BigDecimal rate = exchangeRates.get(rateKey);
        
        if (rate == null) {
            log.warn("Exchange rate not found for {} to {}. Using 1:1 conversion.", fromCurrency, toCurrency);
            return amount; // Return original amount if conversion not available
        }
        
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal convertToDisplayCurrency(BigDecimal amountInUSD) {
        return convertCurrency(amountInUSD, "USD", displayCurrency);
    }
    
    public BigDecimal convertFromDisplayCurrency(BigDecimal amountInDisplayCurrency) {
        return convertCurrency(amountInDisplayCurrency, displayCurrency, "USD");
    }
    
    public String formatPrice(BigDecimal amount, String currency) {
        return String.format("%.0f %s", amount, currency);
    }
    
    public void updateExchangeRate(String fromCurrency, String toCurrency, BigDecimal rate) {
        String rateKey = fromCurrency + "_TO_" + toCurrency;
        exchangeRates.put(rateKey, rate);
        
        // Update the corresponding reverse rate
        String reverseRateKey = toCurrency + "_TO_" + fromCurrency;
        exchangeRates.put(reverseRateKey, BigDecimal.ONE.divide(rate, 6, RoundingMode.HALF_UP));
        
        log.info("Updated exchange rate: {} = {}", rateKey, rate);
    }
    
    // Method to refresh rates from properties
    public void refreshRates() {
        initializeExchangeRates();
    }
} 