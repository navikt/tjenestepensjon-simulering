package no.nav.tjenestepensjon.simulering.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

public class TokenProviderStub {

    public static void configureTokenProviderStub(WireMockServer wireMockServer) {
        wireMockServer.stubFor(WireMock.any(WireMock.urlPathEqualTo("/rest/v1/sts/jwks"))
                .willReturn(WireMock.okJson(getJwksJson())));
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/rest/v1/sts/token"))
                .willReturn(WireMock.okJson("{\"access_token\":\"" + getAccessToken() + "\",\"expires_in\":\"3600\",\"token_type\":\"Bearer\"}")));
    }

    //JWT generated with https://jwt.io
    public static String getAccessToken() {
        return "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImY3M2Y4MWEzLWFmNzEtNDcyNi04NTlmLTRlODAyMmM5MjBkNCJ9"
                + ".eyJzdWIiOiIxMjM0IiwibmFtZSI6InRlc3R1c2VyIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwIn0"
                + ".L7SDfkMqd21Bfmnd4DM66Exzms_YkqQObaWl552nkhJD2jLuqLAPjTNEX1QmOuIXtHySjhB8EybY_P10ID6TWkOaH6XcpCKVlfNWa9pPoG202ir68m0CXTk513BMxdltsQdRNj6LGGbY2j4I"
                + "_hrBtSsPfI1nx7dmR6Vg4nCOrU9bJOosYINDMD1zdY-I3P34sr5WD_nKV8zBvIBruocHRJ_qJD6zdLxdqhT-Zo6v1cGmA-pOGN-0XIUa2lq3_TPNX5ohsE-ZawgQ8v7j5Rl11ZTy83ZCeSG199"
                + "-PKy7F5rwT-Wjpnh2sBAhg3G4SxYM1ziMbmAPGG3nZr7I9fEHphg";
    }

    //JWKS/PEM conversion done with https://8gwifi.org/jwkconvertfunctions.jsp
    public static String getJwksJson() {
        return "{\"keys\":[{\"kty\":\"RSA\",\"e\":\"AQAB\",\"use\":\"sig\",\"kid\":\"f73f81a3-af71-4726-859f-4e8022c920d4\",\"alg\":\"RS256\","
                + "\"n\":\"nzyis1ZjfNB0bBgKFMSvvkTtwlvBsaJq7S5wA-kzeVOVpVWwkWdVha4s38XM_pa_yr47av7"
                + "-z3VTmvDRyAHcaT92whREFpLv9cj5lTeJSibyr_Mrm_YtjCZVWgaOYIhwrXwKLqPr_11inWsAkfIytvHWTxZYEcXLgAXFuUuaS3uF9gEiNQwzGTU1v0FqkqTBr4B8nW3HCN47XUu0t8Y0e"
                + "-lf4s4OxQawWD79J9_5d3Ry0vbV3Am1FtGJiJvOwRsIfVChDpYStTcHTCMqtvWbV6L11BWkpzGXSW4Hv43qa-GSYOD2QU68Mb59oSk2OB-BtOLpJofmbGEGgvmwyCI9Mw\"}]}";
    }
}
