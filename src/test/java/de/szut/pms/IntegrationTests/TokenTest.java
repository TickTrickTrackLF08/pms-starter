package de.szut.pms.IntegrationTests;

import de.szut.pms.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;


public class TokenTest extends AbstractIntegrationTest {

    //Test für wenn kein Token übergeben wurde
    @Test
    void WithNoToken()  throws Exception {
        mockMvc.perform(get("/projects"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/problem+json;charset=UTF-8"))
                .andExpect(jsonPath("title", is("No token is provided")));

    }

    //Test für wenn ein Invalid Token übergeben wurde
    @Test
    void WithInvalidToken() throws Exception {
        mockMvc.perform(get("/projects")
                .header("Authorization", "Bearer 1232155"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/problem+json;charset=UTF-8"))
                .andExpect(jsonPath("title", is("Bearer Token Invalid")));
    }



}
