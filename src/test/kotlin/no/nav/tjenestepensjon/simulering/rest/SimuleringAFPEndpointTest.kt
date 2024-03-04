package no.nav.tjenestepensjon.simulering.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.nav.tjenestepensjon.simulering.defaultFNRString
import no.nav.tjenestepensjon.simulering.model.domain.pen.AfpOffentligLivsvarigYtelseMedDelingstall
import no.nav.tjenestepensjon.simulering.model.domain.pen.Alder
import no.nav.tjenestepensjon.simulering.model.domain.pen.SimulerAFPOffentligLivsvarigResponse
import no.nav.tjenestepensjon.simulering.service.AADClient
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.v2.models.defaultAktiveOrdningerJson
import no.nav.tjenestepensjon.simulering.v2.models.defaultSimulerBeregningAFPOffentligJson
import no.nav.tjenestepensjon.simulering.v3.afp.AFPOffentligLivsvarigSimuleringService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.time.LocalDate

@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class SimuleringAFPEndpointTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var aadClient: AADClient

    //    @MockBean
//    private lateinit var tpClient: TpClient
    @MockBean
    private lateinit var aFPOffentligLivsvarigSimuleringService: AFPOffentligLivsvarigSimuleringService

    private var wireMockServer = WireMockServer().apply {
        start()
        stubFor(get("/api/tjenestepensjon/aktiveOrdninger").willReturn(okJson(defaultAktiveOrdningerJson)))
//        stubFor(post(""))
    }

    @AfterAll
    fun afterAll() {
        wireMockServer.stop()
    }

    @Test
    @WithMockUser
    fun simulerAfpOffentligLivsvarig() {
        Mockito.`when`(aadClient.getToken("api://bogus")).thenReturn("")
        Mockito.`when`(aFPOffentligLivsvarigSimuleringService.simuler(anyNonNull()))
            .thenReturn(
                listOf(AfpOffentligLivsvarigYtelseMedDelingstall(
                    pensjonsbeholdning = 250000,
                    afpYtelsePerAar = 50000.1,
                    delingstall = 18.13,
                    gjelderFraOgMed = LocalDate.of(2027, 1, 1),
                    gjelderFraOgMedAlder = Alder(64, 1)))
            )

        mockMvc.post("/simulering/afp-offentlig-livsvarig") {
            content = defaultSimulerBeregningAFPOffentligJson
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                content {
                    content {
                        """
                    {
                        "fnr": "$defaultFNRString"
                        "ytelser": [
                            {
                                "pensjonsbeholdning": 250000,
                                "afpYtelsePerAar": 50000.1,
                                "delingstall": 18.13,
                                "gjelderFraOgMed": "2027-01-01",
                                "gjelderFraOgMedAlder": {
                                    "ar": 64,
                                    "maaneder": 1
                                }
                            }
                        ],
                        "tpLeverandoerer": "SPK,KLP"
                    }
                    """.trimIndent()
                    }
                }
            }
    }

}