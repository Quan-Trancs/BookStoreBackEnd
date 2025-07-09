package quantran.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataSource dataSource;

    @Test
    void testBasicHealthCheck() throws Exception {
        mockMvc.perform(get("/api/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.application").value("BookStore Backend API"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testDetailedHealthCheck() throws Exception {
        // Mock successful database connection
        Connection mockConnection = org.mockito.Mockito.mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(mockConnection);
        
        mockMvc.perform(get("/api/health/detailed")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.application").value("BookStore Backend API"))
                .andExpect(jsonPath("$.database.status").value("UP"))
                .andExpect(jsonPath("$.database.type").value("PostgreSQL"))
                .andExpect(jsonPath("$.system.os").exists())
                .andExpect(jsonPath("$.system.architecture").exists())
                .andExpect(jsonPath("$.system.processors").exists())
                .andExpect(jsonPath("$.system.memory.max").exists())
                .andExpect(jsonPath("$.system.memory.used").exists())
                .andExpect(jsonPath("$.system.memory.free").exists())
                .andExpect(jsonPath("$.system.memory.usagePercentage").exists())
                .andExpect(jsonPath("$.runtime.uptime").exists())
                .andExpect(jsonPath("$.runtime.totalMemory").exists())
                .andExpect(jsonPath("$.runtime.freeMemory").exists())
                .andExpect(jsonPath("$.runtime.threads").exists());
    }

    @Test
    void testDetailedHealthCheckWithDatabaseFailure() throws Exception {
        // Mock database connection failure
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection failed"));
        
        mockMvc.perform(get("/api/health/detailed")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.database.status").value("DOWN"))
                .andExpect(jsonPath("$.database.error").value("Connection failed"))
                .andExpect(jsonPath("$.system.os").exists())
                .andExpect(jsonPath("$.system.architecture").exists());
    }

    @Test
    void testDatabaseHealthCheckSuccess() throws Exception {
        // Mock successful database connection
        Connection mockConnection = org.mockito.Mockito.mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(mockConnection);
        
        mockMvc.perform(get("/api/health/database")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.database").value("PostgreSQL"))
                .andExpect(jsonPath("$.message").value("Database connection successful"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testDatabaseHealthCheckFailure() throws Exception {
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection failed"));

        mockMvc.perform(get("/api/health/database")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.database").value("PostgreSQL"))
                .andExpect(jsonPath("$.error").value("Connection failed"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testReadinessCheck() throws Exception {
        mockMvc.perform(get("/api/health/ready")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY"))
                .andExpect(jsonPath("$.message").value("Application is ready to serve requests"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testLivenessCheck() throws Exception {
        mockMvc.perform(get("/api/health/live")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ALIVE"))
                .andExpect(jsonPath("$.message").value("Application is alive and responding"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testAsyncHealthCheck() throws Exception {
        mockMvc.perform(get("/api/health/async")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.component").value("Async Processing"))
                .andExpect(jsonPath("$.message").value("Background workers are operational"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
} 