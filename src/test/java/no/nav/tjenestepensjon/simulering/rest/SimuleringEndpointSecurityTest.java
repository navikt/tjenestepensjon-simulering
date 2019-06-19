package no.nav.tjenestepensjon.simulering.rest;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static no.nav.tjenestepensjon.simulering.config.TokenProviderStub.configureTokenProviderStub;
import static no.nav.tjenestepensjon.simulering.config.TokenProviderStub.getAccessToken;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication;

@SpringBootTest(classes = TjenestepensjonSimuleringApplication.class)
@AutoConfigureMockMvc
public class SimuleringEndpointSecurityTest {

    @Autowired
    private MockMvc mockMvc;
    private static WireMockServer wireMockServer;

    @BeforeAll
    static void beforeAll() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/person/null/tpordninger"))
                .willReturn(WireMock.okJson("[{\"tssId\":\"1234\",\"tpId\":\"4321\"}]")));
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/tpleverandoer/4321"))
                .willReturn(WireMock.okJson("{\"KLP\"}")));
        configureTokenProviderStub(wireMockServer);
    }

    @Test
    void insecureEndpointsAccessible() throws Exception {
        mockMvc.perform(get("/actuator/prometheus")).andExpect(status().isOk());
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
        mockMvc.perform(get("/isAlive")).andExpect(status().isOk());
        mockMvc.perform(get("/isReady")).andExpect(status().isOk());
    }

    @Test
    void secureEndpointUnauthorizedWhenNoToken() throws Exception {
        mockMvc.perform(post("/simulering").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void secureEndpointUnauthorizedWhenInvalidToken() throws Exception {
        mockMvc.perform(post("/simulering").contentType(MediaType.APPLICATION_JSON).content("{}")
                .header(AUTHORIZATION, "Bearer abc1234"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void secureEndpointOkWithValidToken() throws Exception {
        mockMvc.perform(post("/simulering").contentType(MediaType.APPLICATION_JSON).content("{}")
                .header(AUTHORIZATION, "Bearer " + getAccessToken()))
                .andExpect(status().isOk());
    }

    @AfterAll
    static void afterAll() {
        wireMockServer.stop();
    }
}
