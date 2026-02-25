package fpt.ntu.vuatrovn.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    // ✅ Public endpoint (signup)
    @Test
    void publicSignupEndpoint_shouldBeAccessible() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "test@gmail.com",
                        "password": "123456"
                    }
                """))
                .andExpect(status().isOk());
    }

    // ✅ Protected endpoint
    @Test
    void protectedEndpoint_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/test/protected"))
                .andExpect(status().isForbidden());
    }
}