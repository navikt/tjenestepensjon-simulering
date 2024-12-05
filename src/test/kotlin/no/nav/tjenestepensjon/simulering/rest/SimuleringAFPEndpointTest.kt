package no.nav.tjenestepensjon.simulering.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.nav.tjenestepensjon.simulering.defaultFNRString
import no.nav.tjenestepensjon.simulering.fnrMedEttMedlemskapITPOrdning
import no.nav.tjenestepensjon.simulering.fnrUtenMedlemskap
import no.nav.tjenestepensjon.simulering.model.domain.pen.AfpOffentligLivsvarigYtelseMedDelingstall
import no.nav.tjenestepensjon.simulering.model.domain.pen.Alder
import no.nav.tjenestepensjon.simulering.service.AADClient
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.v1.consumer.FssGatewayAuthService
import no.nav.tjenestepensjon.simulering.v2.models.defaultAktiveOrdningerJson
import no.nav.tjenestepensjon.simulering.v2.models.defaultSimulerBeregningAFPOffentligJson
import no.nav.tjenestepensjon.simulering.v2.models.enAktivOrdningJson
import no.nav.tjenestepensjon.simulering.v2.models.simulerBeregningAFPOffentligUtenMedlemskapITPOrdningJson
import no.nav.tjenestepensjon.simulering.v2025.afp.v1.AFPOffentligLivsvarigSimuleringService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
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

    @MockitoBean
    private lateinit var aadClient: AADClient

    @MockitoBean
    private lateinit var fssGatewayAuthService: FssGatewayAuthService

    @MockitoBean
    private lateinit var afpOffentligLivsvarigSimuleringService: AFPOffentligLivsvarigSimuleringService

    private var wireMockServer = WireMockServer().apply {
        start()
        stubFor(get("/api/tjenestepensjon/aktiveOrdninger").withHeader("fnr", equalTo(defaultFNRString)).willReturn(okJson(defaultAktiveOrdningerJson)))
        stubFor(get("/api/tjenestepensjon/aktiveOrdninger").withHeader("fnr", equalTo(fnrMedEttMedlemskapITPOrdning)).willReturn(okJson(enAktivOrdningJson)))
        stubFor(get("/api/tjenestepensjon/aktiveOrdninger").withHeader("fnr", equalTo(fnrUtenMedlemskap)).willReturn(okJson("[]")))
    }

    @AfterAll
    fun afterAll() {
        wireMockServer.stop()
    }

    @Test
    @WithMockUser
    fun `simuler AFP Offentlig for bruker med to aktive medlemskap`() {
        Mockito.`when`(aadClient.getToken("api://bogus")).thenReturn("")
        Mockito.`when`(afpOffentligLivsvarigSimuleringService.simuler(anyNonNull()))
            .thenReturn(
                listOf(
                    AfpOffentligLivsvarigYtelseMedDelingstall(
                        pensjonsbeholdning = 250000,
                        afpYtelsePerAar = 50000.1,
                        delingstall = 18.13,
                        gjelderFraOgMed = LocalDate.of(2027, 1, 1),
                        gjelderFraOgMedAlder = Alder(64, 1)
                    )
                )
            )

        mockMvc.post("/simulering/afp-offentlig-livsvarig") {
            content = defaultSimulerBeregningAFPOffentligJson
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                content {
                    json(
                        """
                    {
                        "fnr": "$defaultFNRString",
                        "afpYtelser": [
                            {
                                "pensjonsbeholdning": 250000,
                                "afpYtelsePerAar": 50000.1,
                                "delingstall": 18.13,
                                "gjelderFraOgMed": "2027-01-01",
                                "gjelderFraOgMedAlder": {
                                    "aar": 64,
                                    "maaneder": 1
                                }
                            }
                        ]
                    }
                    """.trimIndent()
                    )
                }
            }
    }

    @Test
    @WithMockUser
    fun `simuler AFP Offentlig for bruker med et medlemskap`() {
        Mockito.`when`(aadClient.getToken("api://bogus")).thenReturn("")
        Mockito.`when`(afpOffentligLivsvarigSimuleringService.simuler(anyNonNull()))
            .thenReturn(
                listOf(
                    AfpOffentligLivsvarigYtelseMedDelingstall(
                        pensjonsbeholdning = 250000,
                        afpYtelsePerAar = 50000.1,
                        delingstall = 18.13,
                        gjelderFraOgMed = LocalDate.of(2027, 1, 1),
                        gjelderFraOgMedAlder = Alder(64, 1)
                    )
                )
            )

        mockMvc.post("/simulering/afp-offentlig-livsvarig") {
            content = defaultSimulerBeregningAFPOffentligJson
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                content {
                    json(
                        """
                    {
                        "fnr": "$defaultFNRString",
                        "afpYtelser": [
                            {
                                "pensjonsbeholdning": 250000,
                                "afpYtelsePerAar": 50000.1,
                                "delingstall": 18.13,
                                "gjelderFraOgMed": "2027-01-01",
                                "gjelderFraOgMedAlder": {
                                    "aar": 64,
                                    "maaneder": 1
                                }
                            }
                        ]
                    }
                    """.trimIndent()
                    )
                }
            }
    }

    @Test
    @WithMockUser
    fun `simuler AFP Offentlig for bruker uten medlemskap`() {
        Mockito.`when`(aadClient.getToken("api://bogus")).thenReturn("")
        Mockito.`when`(afpOffentligLivsvarigSimuleringService.simuler(anyNonNull()))
            .thenReturn(
                listOf(
                    AfpOffentligLivsvarigYtelseMedDelingstall(
                        pensjonsbeholdning = 250000,
                        afpYtelsePerAar = 50000.1,
                        delingstall = 18.13,
                        gjelderFraOgMed = LocalDate.of(2027, 1, 1),
                        gjelderFraOgMedAlder = Alder(64, 1)
                    )
                )
            )

        mockMvc.post("/simulering/afp-offentlig-livsvarig") {
            content = simulerBeregningAFPOffentligUtenMedlemskapITPOrdningJson
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                content {
                    json(
                        """
                    {
                        "fnr": "$fnrUtenMedlemskap",
                        "afpYtelser": [
                            {
                                "pensjonsbeholdning": 250000,
                                "afpYtelsePerAar": 50000.1,
                                "delingstall": 18.13,
                                "gjelderFraOgMed": "2027-01-01",
                                "gjelderFraOgMedAlder": {
                                    "aar": 64,
                                    "maaneder": 1
                                }
                            }
                        ]
                    }
                    """.trimIndent()
                    )
                }
            }
    }

    @Test
    @WithMockUser
    fun `simuler for ung bruker`() {
        Mockito.`when`(aadClient.getToken("api://bogus")).thenReturn("")

        mockMvc.post("/simulering/afp-offentlig-livsvarig") {
            content = """{"fnr": "$fnrUtenMedlemskap",
                            "fodselsdato": "1977-02-01",
                              "fremtidigeInntekter": [
                                {
                                 "belop": 500000,
                                 "fraOgMed": "2028-01-01"
                                },
                                {
                                  "belop": 550000,
                                  "fraOgMed": "2029-01-01"
                                }
                              ],
                              "fom": "2030-01-01"
                            }"""
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    @WithMockUser
    fun `simuler for bruker foedt foer 1963`() {
        Mockito.`when`(aadClient.getToken("api://bogus")).thenReturn("")

        mockMvc.post("/simulering/afp-offentlig-livsvarig") {
            content = """{"fnr": "$fnrUtenMedlemskap",
                            "fodselsdato": "1962-12-31",
                              "fremtidigeInntekter": [
                                {
                                 "belop": 500000,
                                 "fraOgMed": "2028-01-01"
                                },
                                {
                                  "belop": 550000,
                                  "fraOgMed": "2029-01-01"
                                }
                              ],
                              "fom": "2029-01-01"
                            }"""
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isBadRequest() }
            }
    }

}