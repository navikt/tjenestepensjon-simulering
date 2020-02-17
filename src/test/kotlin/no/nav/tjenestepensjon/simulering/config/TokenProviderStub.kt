package no.nav.tjenestepensjon.simulering.config

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock

object TokenProviderStub {

    fun configureTokenProviderStub(wireMockServer: WireMockServer?) {
        wireMockServer!!.stubFor(WireMock.any(WireMock.urlPathEqualTo("/rest/v1/sts/jwks"))
                .willReturn(WireMock.okJson(jwksJson)))
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/rest/v1/sts/token"))
                .willReturn(WireMock.okJson("""{"access_token":"$accessToken","expires_in":"3600","token_type":"Bearer"}""")))
    }

    //JWT generated with https://jwt.io
    val accessToken: String
        get() = """eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImY3M2Y4MWEzLWFmNzEtNDcyNi04NTlmLTRlODAyMmM5MjBkNCJ9
                |.eyJzdWIiOiIxMjM0IiwibmFtZSI6InRlc3R1c2VyIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwIn0.L7SDfkMqd21Bfm
                |nd4DM66Exzms_YkqQObaWl552nkhJD2jLuqLAPjTNEX1QmOuIXtHySjhB8EybY_P10ID6TWkOaH6XcpCKVlfNWa9pPoG202ir68
                |m0CXTk513BMxdltsQdRNj6LGGbY2j4I_hrBtSsPfI1nx7dmR6Vg4nCOrU9bJOosYINDMD1zdY-I3P34sr5WD_nKV8zBvIBruocH
                |RJ_qJD6zdLxdqhT-Zo6v1cGmA-pOGN-0XIUa2lq3_TPNX5ohsE-ZawgQ8v7j5Rl11ZTy83ZCeSG199-PKy7F5rwT-Wjpnh2sBAh
                |g3G4SxYM1ziMbmAPGG3nZr7I9fEHphg""".trimMargin().replace("\n", "")

    //JWKS/PEM conversion done with https://8gwifi.org/jwkconvertfunctions.jsp
    private val jwksJson: String
        get() = """{
            |"keys":[{
                |"kty":"RSA",
                |"e":"AQAB",
                |"use":"sig",
                |"kid":"f73f81a3-af71-4726-859f-4e8022c920d4",
                |"alg":"RS256",
                |"n":"nzyis1ZjfNB0bBgKFMSvvkTtwlvBsaJq7S5wA-kzeVOVpVWwkWdVha4s38XM_pa_yr47av7-z3VTmvDRyAHcaT92whREFp
                    |Lv9cj5lTeJSibyr_Mrm_YtjCZVWgaOYIhwrXwKLqPr_11inWsAkfIytvHWTxZYEcXLgAXFuUuaS3uF9gEiNQwzGTU1v0Fqk
                    |qTBr4B8nW3HCN47XUu0t8Y0e-lf4s4OxQawWD79J9_5d3Ry0vbV3Am1FtGJiJvOwRsIfVChDpYStTcHTCMqtvWbV6L11BWk
                    |pzGXSW4Hv43qa-GSYOD2QU68Mb59oSk2OB-BtOLpJofmbGEGgvmwyCI9Mw"
            |}]}""".trimMargin().replace("\n", "")
}