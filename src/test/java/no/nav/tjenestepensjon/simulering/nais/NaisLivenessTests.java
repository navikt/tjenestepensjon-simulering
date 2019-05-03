package no.nav.tjenestepensjon.simulering.nais;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class NaisLivenessTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void isAlive_returns_OK() throws Exception {
        mockMvc.perform(get("/isAlive")).andExpect(status().isOk());
    }

    @Test
    public void isReady_returns_OK() throws Exception {
        mockMvc.perform(get("/isReady")).andExpect(status().isOk());
    }
}
