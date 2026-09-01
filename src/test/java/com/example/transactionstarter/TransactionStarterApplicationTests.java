package com.example.transactionstarter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// These are integration tests - they test the whole system end to end
@SpringBootTest
@AutoConfigureMockMvc
class TransactionStarterApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private static final String BASE = "/api/transactions";

    // Helper to build a JSON request body for creating a transaction
    private String asJson(String customerId, double amount,
                          String currency, String type) {
        return String.format(
                "{\"customerId\":\"%s\",\"amount\":%s,\"currency\":\"%s\",\"type\":\"%s\"}",
                customerId, new BigDecimal(amount).toPlainString(), currency, type);
    }

    // Test: Creating a transaction should return 201 Created with an ID
    @Test
    void createTransaction_shouldReturn201WithId() throws Exception {
        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson("cust-001", 100.50, "USD", "DEBIT")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.customerId").value("cust-001"))
                .andExpect(jsonPath("$.amount").value(100.50))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    // Test: Getting a transaction by ID should return 200 OK with the data
    @Test
    void getTransaction_shouldReturn200AndTransaction() throws Exception {
        // First create a transaction
        String createResponse = mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson("cust-002", 250.00, "EUR", "CREDIT")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extract the ID from the response
        String id = createResponse.split("\"id\":\"")[1].split("\"")[0];

        // Now fetch it
        mockMvc.perform(get(BASE + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.customerId").value("cust-002"))
                .andExpect(jsonPath("$.amount").value(250.00))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    // Test: Updating a PENDING transaction to COMPLETED should work
    @Test
    void updateStatus_shouldChangeStatusToCompleted() throws Exception {
        // Create a transaction
        String createResponse = mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson("cust-003", 50.00, "GBP", "TRANSFER")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = createResponse.split("\"id\":\"")[1].split("\"")[0];

        // Update it to COMPLETED
        mockMvc.perform(put(BASE + "/{id}/status", id)
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    // Test: Getting all transactions for a customer should return all of them
    @Test
    void getTransactionsByCustomer_shouldReturnAllForCustomer() throws Exception {
        // Create two transactions for the same customer
        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson("cust-004", 10.00, "USD", "DEBIT")))
                .andExpect(status().isCreated());

        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson("cust-004", 20.00, "USD", "CREDIT")))
                .andExpect(status().isCreated());

        // Fetch all transactions for this customer
        mockMvc.perform(get(BASE + "/customer/{customerId}", "cust-004"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // Test: Cannot change status from COMPLETED back to PENDING
    @Test
    void updateStatus_invalidTransition_shouldReturn409() throws Exception {
        // Create a transaction
        String createResponse = mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson("cust-005", 75.00, "USD", "DEBIT")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = createResponse.split("\"id\":\"")[1].split("\"")[0];

        // Mark it as COMPLETED
        mockMvc.perform(put(BASE + "/{id}/status", id)
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk());

        // Try to go back to PENDING - this should fail
        mockMvc.perform(put(BASE + "/{id}/status", id)
                        .param("status", "PENDING"))
                .andExpect(status().isConflict());
    }

    // Test: Getting a non-existent transaction should return 404
    @Test
    void getTransaction_unknownId_shouldReturn404() throws Exception {
        mockMvc.perform(get(BASE + "/non-existent-id"))
                .andExpect(status().isNotFound());
    }

    // Test: Invalid data should be rejected with 400 Bad Request
    @Test
    void createTransaction_invalidData_shouldReturn400() throws Exception {
        // Empty customerId
        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":\"\",\"amount\":100.50,\"currency\":\"USD\",\"type\":\"DEBIT\"}"))
                .andExpect(status().isBadRequest());

        // Negative amount
        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":\"cust-001\",\"amount\":-50,\"currency\":\"USD\",\"type\":\"DEBIT\"}"))
                .andExpect(status().isBadRequest());

        // Currency not 3 characters
        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":\"cust-001\",\"amount\":100.50,\"currency\":\"US\",\"type\":\"DEBIT\"}"))
                .andExpect(status().isBadRequest());
    }

    // Test: Cannot create a transaction with an ID that already exists
    @Test
    void createTransaction_duplicateId_shouldReturn409() throws Exception {
        String clientId = "my-custom-id-123";

        // First create with this custom ID
        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"" + clientId + "\",\"customerId\":\"cust-dup\",\"amount\":100.00,\"currency\":\"USD\",\"type\":\"DEBIT\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(clientId));

        // Try to create another one with the same ID - should be rejected
        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"" + clientId + "\",\"customerId\":\"cust-dup\",\"amount\":200.00,\"currency\":\"USD\",\"type\":\"CREDIT\"}"))
                .andExpect(status().isConflict());
    }
}
