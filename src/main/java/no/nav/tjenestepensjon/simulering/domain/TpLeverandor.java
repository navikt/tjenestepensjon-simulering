package no.nav.tjenestepensjon.simulering.domain;

public class TpLeverandor {
    private final String name;
    private final String url;

    public TpLeverandor(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
