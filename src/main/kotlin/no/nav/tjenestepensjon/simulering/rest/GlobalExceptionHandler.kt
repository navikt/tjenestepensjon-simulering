package no.nav.tjenestepensjon.simulering.rest

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        val errorResponse = e.message ?: "Validation failed"
        return ResponseEntity.badRequest().body(ErrorResponse(errorResponse))
    }

    data class ErrorResponse(val message: String)
}