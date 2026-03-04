package fpt.ntu.vuatrovn.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    // ✅ Test public endpoint (permitAll)
    @Test
    void publicEndpoint_shouldBeAccessible() throws Exception {
        mockMvc.perform(get("/api/auth/anything"))
                .andExpect(status().isNotFound());
        // 404 nghĩa là endpoint không tồn tại
        // nhưng quan trọng là KHÔNG bị 403
    }

    // ✅ Test protected endpoint
    @Test
    void protectedEndpoint_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/test-auth"))
                .andExpect(status().isForbidden());
    }
}