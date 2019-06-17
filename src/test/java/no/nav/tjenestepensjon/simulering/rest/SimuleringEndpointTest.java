package no.nav.tjenestepensjon.simulering.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static no.nav.tjenestepensjon.simulering.config.TokenProviderStub.configureTokenProviderStub;
import static no.nav.tjenestepensjon.simulering.config.TokenProviderStub.getAccessToken;

import com.github.tomakehurst.wiremock.WireMockServer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SimuleringEndpointTest {

    @Autowired
    private MockMvc mockMvc;
    private static WireMockServer wireMockServer;

    @BeforeAll
    static void beforeAll(){
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        configureTokenProviderStub(wireMockServer);
    }

    @Test
    public void endepunkt_kalles_uten_requestbody() throws Exception {
        mockMvc.perform(get("/simulering").header("Authorization", "Bearer " + getAccessToken())).andExpect(status().isBadRequest());
    }

//    @Test
//    public void simulering_returns_OK() throws Exception {
//        mockMvc.perform(get("/simulering").contentType(MediaType.APPLICATION_JSON).content("{ \"fnr\": \"lol\", \"inntekter\": [{ \"inntekt\": 101 }]}"))
//                .andExpect(status().isOk())
//                .andExpect(content().json("{ \"simulertPensjonListe\" : null} "));
//    }

    @AfterAll
    static void afterAll(){
        wireMockServer.stop();
    }
}
