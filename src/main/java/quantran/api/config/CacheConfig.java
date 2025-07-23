package quantran.api.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class CacheConfig {

    @Value("${spring.cache.caffeine.spec:maximumSize=500,expireAfterWrite=10m}")
    private String caffeineSpec;

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // L1: Caffeine
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(Caffeine.from(caffeineSpec));

        // L2: Redis
        RedisCacheManager redisCacheManager = RedisCacheManager.create(redisConnectionFactory);

        // Composite: L1 first, then L2
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager(
                caffeineCacheManager, redisCacheManager
        );
        compositeCacheManager.setFallbackToNoOpCache(false); // throw if cache not found
        return compositeCacheManager;
    }
} 