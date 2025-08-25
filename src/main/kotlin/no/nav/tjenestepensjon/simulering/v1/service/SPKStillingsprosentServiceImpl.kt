package no.nav.tjenestepensjon.simulering.v1.service

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningFullDto
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v1.soap.SPKStillingsprosentSoapClient
import org.springframework.stereotype.Component

@Component
class SPKStillingsprosentServiceImpl(
    private val SPKStillingsprosentSoapClient: SPKStillingsprosentSoapClient,
    private val metrics: AppMetrics
) : StillingsprosentService {
    private val log = KotlinLogging.logger {}

    override fun getStillingsprosentListe(fnr: String, tpOrdning: TpOrdningFullDto): List<Stillingsprosent> {
        metrics.incrementCounter(AppMetrics.Metrics.APP_NAME, AppMetrics.Metrics.APP_TOTAL_OPPTJENINGSPERIODE_CALLS)
        val startTime = metrics.startTime()
        val stillingsprosentList = SPKStillingsprosentSoapClient.getStillingsprosenter(fnr, tpOrdning)
        val elapsed = metrics.elapsedSince(startTime)
        log.info { "Executed call to stillingsprosenter in: $elapsed ms $stillingsprosentList" }
        metrics.incrementCounter(AppMetrics.Metrics.APP_NAME, AppMetrics.Metrics.APP_TOTAL_OPPTJENINGSPERIODE_TIME, elapsed.toDouble())
        return stillingsprosentList
    }
}