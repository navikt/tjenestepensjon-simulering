[![CircleCI](https://circleci.com/gh/navikt/tjenestepensjon-simulering.svg?style=svg)](https://circleci.com/gh/navikt/tjenestepensjon-simulering)
# tjenestepensjon-simulering
Mottar alderspensjonsinformasjon om en gitt person fra PESYS via REST. 
Deretter henter man tjenestepensjonsforholdene til denne personen basert på kall mot tjenestepensjonsregisteret.
Når man har tjenestepensjonsforholdene til denne personen, så gjør man kall til alle tjenestepensjonsordningene for å hente de relaterte stillingsprosentene.
Et simuleringskall blir så gjort mot den ordningen personen sist hadde et forhold til.
Simuleringsresulatet blir så returnert til PESYS.

Input og Output er definert i løsningsbeskrivelse [PK-54410](https://confluence.adeo.no/pages/viewpage.action?pageId=313346461).

#### Eksempel
```bash
curl -k -X GET https://tjenestepensjon-simulering.nais.preprod.local/simulering \
 -H "Content-type: application/json" \
 -H "Accept: application/json" \
 -d '{
     	"fnr": "00000000000",
     	"sivilstandkode": "SAMB",
     	"sprak": "Norsk",
     	"simuleringsperioder": [{
     			"datoFom": 1559904329477,
     			"utg": 50,
     			"stillingsprosentOffentlig": 50,
     			"poengArTom1991": 30,
     			"poengArFom1992": 20,
     			"sluttpoengtall": 50.0,
     			"anvendtTrygdetid": 10,
     			"forholdstall": 2.7,
     			"delingstall": 5.1,
     			"uforegradVedOmregning": 20,
     			"delytelser": [{
     					"pensjonstype": "ALDER",
     					"belop": 30.0
     				}
     			]
     		}
     	],
     	"simulertAFPOffentlig": 20,
     	"simulertAFPPrivat": {
     		"afpOpptjeningTotalbelop": 5000,
     		"kompensasjonstillegg": 20.0
     	},
     	"pensjonsbeholdningperioder": [{
     			"pensjonsbeholdning": 5000.0,
     			"garantipensjonsbeholdning": 10000.0,
     			"garantitilleggsbeholdning": 5000.0,
     			"datoFom": 1559904329477
     		}
     	],
     	"inntekter": [{
     			"datoFom": 1559904329477,
     			"belop": 2000.0
     		}
     	]
     }'
     
# Output:
# {
#	"simulertPensjonListe": [{
#			"tpnr": "...",
#			"navnOrdning": "...",
#			"inkluderteOrdninger": ["..."]
#			"leverandorUrl": "...",
#			"inkluderteTpnr": ["..."],
#			"utelatteTpnr": ["..."],
#			"status": "...",
#			"feilkode": "...",
#			"feilbeskrivelse": "...",
#			"utbetalingsperioder": [{
#					"startDato": "...",
#					"sluttDato": "...",
#					"grad": 0,
#					"arligUtbetaling": 0.0,
#					"ytelsekode": "...",
#					"mangelfullSimuleringkode": "..."
#				}
#			]
#		}
#	]
# }
```

#### Metrikker
Grafana dashboards brukes for å f.eks. monitorere minne, cpu-bruk og andre metrikker.
Se [tjenestepensjon-simulering grafana dasboard](https://grafana.adeo.no/d/BFoD2vmWk/tjenestepensjon-simulering)

#### Logging
[Kibana](https://logs.adeo.no/app/kibana) benyttes til logging. Søk på f.eks. ```application:tjenestepensjon-simulering AND envclass:q``` for logginnslag fra preprod.

#### Kontakt
Kontakt Team Peon dersom du har noen spørsmål. Vi finnes blant annet på Slack, i kanalen [#peon](https://nav-it.slack.com/messages/C6M80587R/)
