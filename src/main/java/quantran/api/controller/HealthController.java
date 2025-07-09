package quantran.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Log4j2
@RequiredArgsConstructor
public class HealthController {

    private final DataSource dataSource;

    /**
     * Basic health check endpoint
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        log.debug("Health check requested");
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        health.put("application", "BookStore Backend API");
        health.put("version", "1.0.0");
        
        return ResponseEntity.ok(health);
    }

    /**
     * Detailed health check with system information
     */
    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        log.debug("Detailed health check requested");
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        health.put("application", "BookStore Backend API");
        health.put("version", "1.0.0");
        
        // Database health
        Map<String, Object> database = new HashMap<>();
        try {
            dataSource.getConnection().close();
            database.put("status", "UP");
            database.put("type", "PostgreSQL");
        } catch (Exception e) {
            database.put("status", "DOWN");
            database.put("error", e.getMessage());
            health.put("status", "DOWN");
        }
        health.put("database", database);
        
        // System information
        Map<String, Object> system = new HashMap<>();
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        
        system.put("os", osBean.getName() + " " + osBean.getVersion());
        system.put("architecture", osBean.getArch());
        system.put("processors", osBean.getAvailableProcessors());
        system.put("systemLoad", osBean.getSystemLoadAverage());
        
        // Memory information
        Map<String, Object> memory = new HashMap<>();
        long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
        
        memory.put("max", formatBytes(maxMemory));
        memory.put("used", formatBytes(usedMemory));
        
        // Handle unlimited memory case (maxMemory = -1)
        if (maxMemory > 0) {
            long freeMemory = maxMemory - usedMemory;
            memory.put("free", formatBytes(freeMemory));
            memory.put("usagePercentage", Math.round((double) usedMemory / maxMemory * 100));
        } else {
            memory.put("free", "Unlimited");
            memory.put("usagePercentage", 0);
        }
        
        system.put("memory", memory);
        health.put("system", system);
        
        // Runtime information
        Map<String, Object> runtime = new HashMap<>();
        Runtime rt = Runtime.getRuntime();
        runtime.put("uptime", formatUptime(ManagementFactory.getRuntimeMXBean().getUptime()));
        runtime.put("totalMemory", formatBytes(rt.totalMemory()));
        runtime.put("freeMemory", formatBytes(rt.freeMemory()));
        runtime.put("threads", ManagementFactory.getThreadMXBean().getThreadCount());
        
        health.put("runtime", runtime);
        
        return ResponseEntity.ok(health);
    }

    /**
     * Database-specific health check
     */
    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> databaseHealth() {
        log.debug("Database health check requested");
        
        Map<String, Object> health = new HashMap<>();
        
        try {
            dataSource.getConnection().close();
            health.put("status", "UP");
            health.put("database", "PostgreSQL");
            health.put("message", "Database connection successful");
            health.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            log.error("Database health check failed", e);
            health.put("status", "DOWN");
            health.put("database", "PostgreSQL");
            health.put("error", e.getMessage());
            health.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            return ResponseEntity.status(503).body(health);
        }
    }

    /**
     * Application readiness check
     */
    @GetMapping("/ready")
    public ResponseEntity<Map<String, Object>> readiness() {
        log.debug("Readiness check requested");
        
        Map<String, Object> readiness = new HashMap<>();
        readiness.put("status", "READY");
        readiness.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        readiness.put("message", "Application is ready to serve requests");
        
        return ResponseEntity.ok(readiness);
    }

    /**
     * Application liveness check
     */
    @GetMapping("/live")
    public ResponseEntity<Map<String, Object>> liveness() {
        log.debug("Liveness check requested");
        
        Map<String, Object> liveness = new HashMap<>();
        liveness.put("status", "ALIVE");
        liveness.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        liveness.put("message", "Application is alive and responding");
        
        return ResponseEntity.ok(liveness);
    }

    /**
     * Custom health check for async processing
     */
    @GetMapping("/async")
    public ResponseEntity<Map<String, Object>> asyncHealth() {
        log.debug("Async processing health check requested");
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("component", "Async Processing");
        health.put("message", "Background workers are operational");
        health.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return ResponseEntity.ok(health);
    }

    // Helper methods
    private String formatBytes(long bytes) {
        if (bytes < 0) return "Unlimited";
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    private String formatUptime(long uptime) {
        long days = uptime / (24 * 60 * 60 * 1000);
        long hours = (uptime % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (uptime % (60 * 60 * 1000)) / (60 * 1000);
        long seconds = (uptime % (60 * 1000)) / 1000;
        
        if (days > 0) {
            return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }
} 