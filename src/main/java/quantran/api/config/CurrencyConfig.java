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
    
    public CurrencyConfig() {
        // Initialize with default exchange rates
        exchangeRates.put("USD_TO_VND", BigDecimal.valueOf(23000));
        exchangeRates.put("VND_TO_USD", BigDecimal.valueOf(1.0 / 23000));
        exchangeRates.put("EUR_TO_VND", BigDecimal.valueOf(25000));
        exchangeRates.put("VND_TO_EUR", BigDecimal.valueOf(1.0 / 25000));
    }
    
    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        
        String rateKey = fromCurrency + "_TO_" + toCurrency;
        BigDecimal rate = exchangeRates.get(rateKey);
        
        if (rate == null) {
            log.warn("Exchange rate not found for {} to {}", fromCurrency, toCurrency);
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
        log.info("Updated exchange rate: {} = {}", rateKey, rate);
    }
} 