package no.nav.tjenestepensjon.simulering.rest

import com.github.tomakehurst.wiremock.WireMockServer
import no.nav.tjenestepensjon.simulering.model.domain.pen.Alder
import no.nav.tjenestepensjon.simulering.service.AADClient
import no.nav.tjenestepensjon.simulering.v2.models.defaultSimulerTjenestepensjonHosSPKJson
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Maanedsutbetaling
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjonMedMaanedsUtbetalinger
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.Tjenestepensjon2025AggregatorTest.Companion.MAANEDER_I_AAR
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.BrukerErIkkeMedlemException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TpOrdningStoettesIkkeException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025Service
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class TjenestepensjonSimuleringV2025ControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var aadClient: AADClient

    @MockBean
    private lateinit var service: TjenestepensjonV2025Service

    private var wireMockServer = WireMockServer().apply {
        start()
    }

    @AfterAll
    fun afterAll() {
        wireMockServer.stop()
    }

    @Test
    @WithMockUser
    fun `test simulering med beregnet resultat`() {
        val perioder = listOf(
            Maanedsutbetaling(
                fraOgMedDato = LocalDate.parse("2026-02-01"),
                fraOgMedAlder = Alder(62, 1),
                maanedsBeloep = 1000
            ),
            Maanedsutbetaling(
                fraOgMedDato = LocalDate.parse("2027-05-01"),
                fraOgMedAlder = Alder(63, 4),
                maanedsBeloep = 2000
            ),
            Maanedsutbetaling(
                fraOgMedDato = LocalDate.parse("2027-08-01"),
                fraOgMedAlder = Alder(63, 7),
                maanedsBeloep = 3000
            )
        )
        val mockRespons = SimulertTjenestepensjonMedMaanedsUtbetalinger(
            tpLeverandoer = "pensjonskasse",
            ordningsListe = emptyList(),
            utbetalingsperioder = perioder,
            aarsakIngenUtbetaling = emptyList()
        )

        `when`(service.simuler(any())).thenReturn(Result.success(mockRespons))
        `when`(aadClient.getToken("api://bogus")).thenReturn("")
        val aarsBeloepVed63 = perioder[0].maanedsBeloep * (perioder[1].fraOgMedAlder.maaneder) +
                perioder[1].maanedsBeloep * (perioder[2].fraOgMedAlder.maaneder - perioder[1].fraOgMedAlder.maaneder) +
                perioder[2].maanedsBeloep * (MAANEDER_I_AAR - perioder[2].fraOgMedAlder.maaneder)

        mockMvc.post("/v2025/tjenestepensjon/v1/simulering") {
            content = defaultSimulerTjenestepensjonHosSPKJson
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                content {
                    json(
                        """
                    {
                        "simuleringsResultatStatus": {
                            "resultatType": "SUCCESS",
                            "feilmelding": null
                        },
                        "simuleringsResultat": {
                                "tpLeverandoer": "${mockRespons.tpLeverandoer}",
                                "utbetalingsperioder": [
                                    {
                                        "aar": 62,
                                        "beloep": 11000
                                    },
                                    {
                                        "aar": 63,
                                        "beloep": $aarsBeloepVed63
                                    },
                                    {
                                        "aar": 64,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 65,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 66,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 67,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 68,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 69,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 70,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 71,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 72,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 73,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 74,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 75,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 76,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 77,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 78,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 79,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 80,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 81,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 82,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 83,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 84,
                                        "beloep": 36000
                                    },
                                    {
                                        "aar": 85,
                                        "beloep": 36000
                                    }
                                ]
                        }
                    }
                    """.trimIndent()
                    )
                }
            }
    }

    @Test
    @WithMockUser
    fun `test simulering naar bruker ikke er medlem`() {
        `when`(service.simuler(any())).thenReturn(Result.failure(BrukerErIkkeMedlemException()))
        `when`(aadClient.getToken("api://bogus")).thenReturn("")

        mockMvc.post("/v2025/tjenestepensjon/v1/simulering") {
            content = defaultSimulerTjenestepensjonHosSPKJson
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                content {
                    json(
                        """
                    {
                        "simuleringsResultatStatus": {
                            "resultatType": "ERROR",
                            "feilmelding": "Bruker er ikke medlem av en offentlig tjenestepensjonsordning"
                        },
                        "simuleringsResultat": null
                    }
                    """.trimIndent()
                    )
                }
            }
    }

    @Test
    @WithMockUser
    fun `test simulering naar tp-ordning ikke stoettes`() {
        val tpOrdning = "opf"
        `when`(service.simuler(any())).thenReturn(Result.failure(TpOrdningStoettesIkkeException(tpOrdning)))
        `when`(aadClient.getToken("api://bogus")).thenReturn("")

        mockMvc.post("/v2025/tjenestepensjon/v1/simulering") {
            content = defaultSimulerTjenestepensjonHosSPKJson
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                content {
                    json(
                        """
                    {
                        "simuleringsResultatStatus": {
                            "resultatType": "ERROR",
                            "feilmelding": "$tpOrdning støtter ikke simulering av tjenestepensjon v2025"
                        },
                        "simuleringsResultat": null
                    }
                    """.trimIndent()
                    )
                }
            }
    }

    @Test
    @WithMockUser
    fun `test simulering naar tpregisteret feiler`() {
        `when`(service.simuler(any())).thenReturn(Result.failure(ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)))
        `when`(aadClient.getToken("api://bogus")).thenReturn("")

        mockMvc.post("/v2025/tjenestepensjon/v1/simulering") {
            content = defaultSimulerTjenestepensjonHosSPKJson
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                content {
                    json(
                        """
                    {
                        "simuleringsResultatStatus": {
                            "resultatType": "ERROR",
                            "feilmelding": "500 INTERNAL_SERVER_ERROR"
                        },
                        "simuleringsResultat": null
                    }
                    """.trimIndent()
                    )
                }
            }
    }
}