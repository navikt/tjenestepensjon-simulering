package no.nav.tjenestepensjon.simulering;

import static no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse;
import no.nav.tjenestepensjon.simulering.rest.RestClient;
import no.nav.tjenestepensjon.simulering.soap.SoapClient;

@Component
public class TjenestepensjonsimuleringEndpointRouter {

    private final SoapClient soapClient;
    private final RestClient restClient;

    public TjenestepensjonsimuleringEndpointRouter(SoapClient soapClient, RestClient restClient) {
        this.soapClient = soapClient;
        this.restClient = restClient;
    }

    public List<Stillingsprosent> getStillingsprosenter(String fnr, TPOrdning tpOrdning, TpLeverandor tpLeverandor) {
        if (tpLeverandor.getImpl() == SOAP) {
            return soapClient.getStillingsprosenter(fnr, tpOrdning, tpLeverandor);
        } else {
            return restClient.getStillingsprosenter(fnr, tpOrdning, tpLeverandor);
        }
    }

    public List<OutgoingResponse.SimulertPensjon> simulerPensjon(IncomingRequest request, TPOrdning tpOrdning,
            TpLeverandor tpLeverandor, Map<TPOrdning, List<Stillingsprosent>> tpOrdningStillingsprosentMap) {
        if (tpLeverandor.getImpl() == SOAP) {
            return soapClient.simulerPensjon(request, tpOrdning, tpLeverandor, tpOrdningStillingsprosentMap);
        } else {
            return restClient.simulerPensjon(request, tpOrdning, tpLeverandor, tpOrdningStillingsprosentMap);
        }
    }
}