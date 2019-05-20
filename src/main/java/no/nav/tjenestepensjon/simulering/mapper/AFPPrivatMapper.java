package no.nav.tjenestepensjon.simulering.mapper;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.ObjectFactory;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.SimulertAFPPrivat;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;

public class AFPPrivatMapper {

    public SimulertAFPPrivat mapToSimulertAFPPrivat(IncomingRequest.SimulerAfpPrivat afpPrivat) {
        if (afpPrivat == null)
            return null;

        var mappedAfpPrivat = new ObjectFactory().createSimulertAFPPrivat();
        mappedAfpPrivat.setAfpOpptjeningTotalbelop(afpPrivat.getAfpOpptjeningTotalbelop());
        mappedAfpPrivat.setKompensasjonstillegg(afpPrivat.getKompensasjonstillegg());

        return mappedAfpPrivat;
    }
}
