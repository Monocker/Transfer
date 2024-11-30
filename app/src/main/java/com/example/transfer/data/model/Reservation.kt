package com.example.transfer.data.model

data class Reservation(
    val id: String = "",
    val zone: String,
    val agency: String = "",
    val hotel: String = "",
    val store: String = "",
    val pickUpTime: String = "",
    val date: String = "",
    val model: String,
    val pax: Int = 0,
    val adults: Int = 0,
    val children: Int = 0,
    val clientName: String = "",
    val seats: List<String> = emptyList(),
    val totalSeats: Int = 0,
    val reservedSeats: Int = 0,
    val availableSeats: Int = 0,
    val seatStates: MutableMap<String, String> = mutableMapOf()
)
