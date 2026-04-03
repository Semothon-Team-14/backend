package semo.backend.dto

import java.time.LocalDate
import java.time.LocalDateTime

data class TicketTripDraftDto(
    val title: String,
    val cityId: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val departureDateTime: LocalDateTime?,
    val departureLandingDateTime: LocalDateTime?,
    val passengerName: String,
    val fromAirportCode: String,
    val toAirportCode: String,
    val operatingCarrierDesignator: String,
    val flightNumber: String,
    val barcodeFormat: String,
    val rawData: String,
)
