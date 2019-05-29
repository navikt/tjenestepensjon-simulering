package no.nav.tjenestepensjon.simulering.service;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SimulerPensjonServiceImpl implements SimulerPensjonService {

    private static final Logger LOG = LoggerFactory.getLogger(SimulerPensjonServiceImpl.class);

    @Override
    public SimulerPensjonResponse simulerPensjon(List<TPOrdning> tpOrdningList, TPOrdning tpLatest) {

        return new SimulerPensjonResponse(null, null);
    }
}
