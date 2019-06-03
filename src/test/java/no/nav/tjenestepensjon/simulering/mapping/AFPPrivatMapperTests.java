package no.nav.tjenestepensjon.simulering.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static no.nav.tjenestepensjon.simulering.mapper.AFPPrivatMapper.mapToSimulertAFPPrivat;

import org.junit.jupiter.api.Test;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.ObjectFactory;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;

public class AFPPrivatMapperTests {

    @Test
    public void test() {
        var original = new IncomingRequest.SimulerAfpPrivat();
        original.setAfpOpptjeningTotalbelop(200000);
        original.setKompensasjonstillegg(1000.0);

        var excpected = new ObjectFactory().createSimulertAFPPrivat();
        excpected.setAfpOpptjeningTotalbelop(200000);
        excpected.setKompensasjonstillegg(1000.0);

        var mapped = mapToSimulertAFPPrivat(original);

        assertEquals(excpected.getAfpOpptjeningTotalbelop(), mapped.getAfpOpptjeningTotalbelop());
        assertEquals(excpected.getKompensasjonstillegg(), mapped.getKompensasjonstillegg());
    }
}
