# tjenestepensjon-simulering
Mottar alderspensjonsinformasjon om en gitt person fra pesys via rest. 
Deretter henter man tjenestepensjonsforholdene til denne personen basert på kall mot tjenestepensjonsregisteret.
Når man har tjenestepensjonsforholdene til denne personen, så gjør man kall til alle tjenestepensjonsordningene for å hente de relaterte stillingsprosentene.
Et simuleringskall blir så gjort mot den ordningen personen sist hadde et forhold til.
Simuleringsresulatet blir så returnert til pesys.

#### Metrikker
Grafana dashboards brukes for å f.eks. monitorere minne, cpu-bruk og andre metrikker.
Se [tjenestepensjon-simulering grafana dasboard](https://grafana.adeo.no/d/BFoD2vmWk/tjenestepensjon-simulering)

#### Logging
[Kibana](https://logs.adeo.no/app/kibana) benyttes til logging. Søk på f.eks. ```application:tjenestepensjon-simulering AND envclass:q``` for logginnslag fra preprod.

#### Bygging
Jenkins benyttes til bygging. Status på bygg finner du her: [tjenestepensjon-simulering jenkins](https://jenkins-peon.adeo.no/job/samordning-hendelse-innlastning/)

Kontakt Team Peon dersom du har noen spørsmål. Vi finnes blant annet på Slack, i kanalen [#peon](https://nav-it.slack.com/messages/C6M80587R/)
