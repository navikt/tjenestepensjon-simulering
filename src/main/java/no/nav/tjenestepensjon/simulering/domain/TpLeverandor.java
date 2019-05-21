package no.nav.tjenestepensjon.simulering.domain;

public class TpLeverandor {
    private final String name;
    private final String url;
    private final EndpointImpl impl;

    public TpLeverandor(String name, String url, EndpointImpl impl) {
        this.name = name;
        this.url = url;
        this.impl = impl;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public EndpointImpl getImpl() {
        return impl;
    }

    public enum EndpointImpl {
        SOAP,
        REST
    }
}
