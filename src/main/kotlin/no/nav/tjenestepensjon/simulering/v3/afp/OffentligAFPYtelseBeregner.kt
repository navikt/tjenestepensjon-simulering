package no.nav.tjenestepensjon.simulering.v3.afp

object OffentligAFPYtelseBeregner {
    private val opptjeningssatsAFPBeholdning = 0.0421
    private val opptjeningssatsPensjonsbeholdning = 0.181

    fun beregn(pensjonsBeholdning: Int, delingstall: Double): Double {
        return (pensjonsBeholdning / opptjeningssatsPensjonsbeholdning) * opptjeningssatsAFPBeholdning / delingstall
    }
}