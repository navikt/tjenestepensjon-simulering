package no.nav.tjenestepensjon.simulering.v3.afp

import no.nav.tjenestepensjon.simulering.model.domain.AfpBeregningsgrunnlag
import no.nav.tjenestepensjon.simulering.model.domain.pen.AfpOffentligLivsvarigYtelseMedDelingstall

object OffentligAFPYtelseBeregner {
    private val opptjeningssatsAFPBeholdning = 0.0421
    private val opptjeningssatsPensjonsbeholdning = 0.181

    fun beregnAfpOffentligLivsvarigYtelser(
        grunnlag: List<AfpBeregningsgrunnlag>
    ): List<AfpOffentligLivsvarigYtelseMedDelingstall> {
        val ytelseFraOnsketUttaksdato = AfpOffentligLivsvarigYtelseMedDelingstall(
            grunnlag[0].pensjonsbeholdning,
            beregn(grunnlag[0].pensjonsbeholdning, grunnlag[0].delingstall),
            grunnlag[0].delingstall,
            grunnlag[0].alderForDelingstall.datoVedAlder,
            grunnlag[0].alderForDelingstall.alder
        )

        if (grunnlag.size == 2) {
            val andreArsYtelse = AfpOffentligLivsvarigYtelseMedDelingstall(
                grunnlag[1].pensjonsbeholdning,
                beregn(grunnlag[1].pensjonsbeholdning - grunnlag[0].pensjonsbeholdning, grunnlag[1].delingstall) + ytelseFraOnsketUttaksdato.afpYtelsePerAar,
                grunnlag[1].delingstall,
                grunnlag[1].alderForDelingstall.datoVedAlder,
                grunnlag[1].alderForDelingstall.alder
            )
            return listOf(ytelseFraOnsketUttaksdato, andreArsYtelse)
        }

        return listOf(ytelseFraOnsketUttaksdato)
    }

    fun beregn(pensjonsBeholdning: Int, delingstall: Double): Double {
        return (pensjonsBeholdning / opptjeningssatsPensjonsbeholdning) * opptjeningssatsAFPBeholdning / delingstall
    }
}