package no.nav.tjenestepensjon.simulering;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse;
import no.nav.tjenestepensjon.simulering.rest.RestClient;
import no.nav.tjenestepensjon.simulering.soap.SoapClient;
import org.springframework.stereotype.Component;

import java.util.List;

import static no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP;

@Component
public class TjenestepensjonsimuleringEndpointRouter implements Tjenestepensjonsimulering {

    private final SoapClient soapClient;
    private final RestClient restClient;

    public TjenestepensjonsimuleringEndpointRouter(SoapClient soapClient, RestClient restClient) {
        this.soapClient = soapClient;
        this.restClient = restClient;
    }

    @Override
    public List<Stillingsprosent> getStillingsprosenter(String fnr, TPOrdning tpOrdning) {

        List<Stillingsprosent> stillingsprosenter;

        TpLeverandor tpLeverandor = tpOrdning.getTpLeverandor();
        TpLeverandor.EndpointImpl endpointimpl = tpLeverandor.getImpl();

        if (endpointimpl == SOAP) {
            System.out.println("Alternative 1 happened");
            stillingsprosenter = soapClient.getStillingsprosenter(fnr, tpOrdning);
        }
        else {
            System.out.println("Alternative 2 happened");
            stillingsprosenter = restClient.getStillingsprosenter(fnr, tpOrdning);
        }

        return stillingsprosenter;
    }

    @Override
    public List<OutgoingResponse.SimulertPensjon> simulerPensjon(IncomingRequest request, List<TPOrdning> tpOrdningList, TPOrdning latest) {
        List<OutgoingResponse.SimulertPensjon> simuletPensjonListe;

        TpLeverandor tpLeverandor = latest.getTpLeverandor();
        TpLeverandor.EndpointImpl endpointimpl = tpLeverandor.getImpl();

        if (endpointimpl == SOAP) {
            simuletPensjonListe = soapClient.simulerPensjon(request, tpOrdningList, latest);
        } else {
            simuletPensjonListe = restClient.simulerPensjon(request, tpOrdningList, latest);
        }

        return simuletPensjonListe;

    }
}