package no.nav.tjenestepensjon.simulering;

import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_TOTAL_SIMULERING_CALLS;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_TOTAL_SIMULERING_TIME;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_CALLS;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_TIME;
import static no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse.SimulertPensjon;
import no.nav.tjenestepensjon.simulering.rest.RestClient;
import no.nav.tjenestepensjon.simulering.soap.SoapClient;

@Component
public class TjenestepensjonsimuleringEndpointRouter {

    private static final Logger LOG = LoggerFactory.getLogger(TjenestepensjonsimuleringEndpointRouter.class);
    private final SoapClient soapClient;
    private final RestClient restClient;
    private final TjenestepensjonSimuleringMetrics metrics;

    public TjenestepensjonsimuleringEndpointRouter(SoapClient soapClient, RestClient restClient, TjenestepensjonSimuleringMetrics metrics) {
        this.soapClient = soapClient;
        this.restClient = restClient;
        this.metrics = metrics;
    }

    public List<Stillingsprosent> getStillingsprosenter(String fnr, TPOrdning tpOrdning, TpLeverandor tpLeverandor) {
        List<Stillingsprosent> stillingsprosentList;
        metrics.incrementCounter(tpLeverandor.getName(), TP_TOTAL_STILLINGSPROSENT_CALLS);
        long startTime = metrics.startTime();
        LOG.info("{} getting stillingsprosenter from: {}", Thread.currentThread().getName(), tpLeverandor);

        if (tpLeverandor.getImpl() == SOAP) {
            stillingsprosentList = soapClient.getStillingsprosenter(fnr, tpOrdning, tpLeverandor);
        } else {
            stillingsprosentList = restClient.getStillingsprosenter(fnr, tpOrdning, tpLeverandor);
        }

        long elapsed = metrics.elapsedSince(startTime);
        metrics.incrementCounter(tpLeverandor.getName(), TP_TOTAL_STILLINGSPROSENT_TIME, elapsed);
        LOG.info("Retrieved stillingsprosenter from: {} in: {} ms", tpLeverandor, elapsed);
        return stillingsprosentList;
    }

    public List<SimulertPensjon> simulerPensjon(IncomingRequest request, TPOrdning tpOrdning,
            TpLeverandor tpLeverandor, Map<TPOrdning, List<Stillingsprosent>> tpOrdningStillingsprosentMap) {
        List<SimulertPensjon> simulertPensjonList;
        metrics.incrementCounter(tpLeverandor.getName(), TP_TOTAL_SIMULERING_CALLS);
        long startTime = metrics.startTime();
        LOG.info("{} getting simulering from: {}", Thread.currentThread().getName(), tpLeverandor);

        if (tpLeverandor.getImpl() == SOAP) {
            simulertPensjonList = soapClient.simulerPensjon(request, tpOrdning, tpLeverandor, tpOrdningStillingsprosentMap);
        } else {
            simulertPensjonList = restClient.simulerPensjon(request, tpOrdning, tpLeverandor, tpOrdningStillingsprosentMap);
        }

        long elapsed = metrics.elapsedSince(startTime);
        metrics.incrementCounter(tpLeverandor.getName(), TP_TOTAL_SIMULERING_TIME, elapsed);
        LOG.info("Retrieved simulation from: {} in: {} ms", tpLeverandor, elapsed);
        return simulertPensjonList;
    }
}