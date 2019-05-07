package no.nav.tjenestepensjon.simulering.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class SimuleringEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void endepunkt_kalles_uten_requestbody() throws Exception {
        mockMvc.perform(get("/simulering")).andExpect(status().isBadRequest());
    }

    @Test
    public void simulering_returns_OK() throws Exception {
        mockMvc.perform(get("/simulering").contentType(MediaType.APPLICATION_JSON).content("{ \"fnr\": \"lol\", \"inntekter\": [{ \"inntekt\": 101 }]}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{ \"simulertPensjonListe\" : null} "));
    }
}
