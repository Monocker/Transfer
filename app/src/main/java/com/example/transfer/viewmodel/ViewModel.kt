package com.example.transfer.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class ReservationViewModel : ViewModel() {
    private val preferencesKeyReservedSeats = "reserved_seats_"
    private val preferencesKeyAvailableSeats = "available_seats_"
    private val preferencesKeySeatStates = "seat_states_"
    private val preferencesKeyReservations = "reservations_initialized"

    // Lista observable para que Compose detecte los cambios
    val reservations = mutableStateListOf<Reservation>()

    // Inicializar reservas desde SharedPreferences o valores predeterminados
    fun initializeReservations(context: Context, defaultSeats: Int = 16) {
        val sharedPreferences = context.getSharedPreferences("ReservationPrefs", Context.MODE_PRIVATE)

        // Verificar si ya están inicializadas las reservas
        val isInitialized = sharedPreferences.getBoolean(preferencesKeyReservations, false)

        if (!isInitialized) {
            // Modelos y zonas para inicializar
            val models = listOf("Van", "Transit", "Rifter")
            val zones = listOf("Cancún", "Puerto Morelos", "Rivera Maya")

            val defaultReservations = mutableListOf<Reservation>()
            models.forEach { model ->
                zones.forEach { zone ->
                    defaultReservations.add(
                        Reservation(
                            title = model,
                            zone = zone,
                            totalSeats = defaultSeats,
                            reservedSeats = 0,
                            availableSeats = defaultSeats
                        )
                    )
                }
            }

            reservations.addAll(defaultReservations)
            saveAllReservationsToPreferences(sharedPreferences)
            sharedPreferences.edit().putBoolean(preferencesKeyReservations, true).apply()
            Log.d("ReservationViewModel", "Inicialización completa de reservas: $reservations")
        } else {
            // Cargar reservas desde SharedPreferences
            loadReservationsFromPreferences(sharedPreferences)
            Log.d("ReservationViewModel", "Reservas cargadas desde SharedPreferences: $reservations")
        }
    }



    // Guardar todas las reservas en SharedPreferences
    private fun saveAllReservationsToPreferences(sharedPreferences: SharedPreferences) {
        val editor = sharedPreferences.edit()
        reservations.forEach { reservation ->
            editor.putInt(preferencesKeyReservedSeats + reservation.title, reservation.reservedSeats)
            editor.putInt(preferencesKeyAvailableSeats + reservation.title, reservation.availableSeats)
        }
        editor.apply()
        Log.d("ReservationViewModel", "Reservas guardadas correctamente en SharedPreferences")
    }

    // Cargar todas las reservas desde SharedPreferences
    fun loadReservationsFromPreferences(sharedPreferences: SharedPreferences) {
        reservations.clear()
        val models = listOf("Van", "Transit", "Rifter")
        val zones = listOf("Cancún", "Puerto Morelos", "Rivera Maya")

        models.forEach { model ->
            zones.forEach { zone ->
                val reservedSeats = sharedPreferences.getInt("$preferencesKeyReservedSeats$model$zone", 0)
                val totalSeats = when (model) {
                    "Van" -> 15
                    "Transit" -> 12
                    "Rifter" -> 10
                    else -> 0
                }
                val availableSeats = totalSeats - reservedSeats

                reservations.add(
                    Reservation(
                        title = model,
                        zone = zone,
                        totalSeats = totalSeats,
                        reservedSeats = reservedSeats,
                        availableSeats = availableSeats,
                        seatStates = getSeatStates("$model$zone", sharedPreferences)
                    )
                )
            }
        }
        Log.d("ReservationViewModel", "Reservas cargadas correctamente: $reservations")
    }



    // Actualizar una reserva específica
    fun updateReservation(context: Context, title: String, zone: String, reservedSeats: Int) {
        val sharedPreferences = context.getSharedPreferences("ReservationPrefs", Context.MODE_PRIVATE)
        val index = reservations.indexOfFirst { it.title == title && it.zone == zone }

        if (index >= 0) {
            val totalSeats = reservations[index].totalSeats
            reservations[index] = reservations[index].copy(
                reservedSeats = reservedSeats,
                availableSeats = totalSeats - reservedSeats
            )
            saveReservationToPreferences(sharedPreferences, reservations[index])
            Log.d("ReservationViewModel", "Reserva actualizada: ${reservations[index]}")
        } else {
            Log.e("ReservationViewModel", "No se encontró reserva para $title en $zone")
        }
    }


    // Guardar una reserva específica en SharedPreferences
    private fun saveReservationToPreferences(sharedPreferences: SharedPreferences, reservation: Reservation) {
        val editor = sharedPreferences.edit()
        editor.putInt(preferencesKeyReservedSeats + reservation.title, reservation.reservedSeats)
        editor.putInt(preferencesKeyAvailableSeats + reservation.title, reservation.availableSeats)
        editor.apply()
    }

    // Obtener estados de los asientos desde SharedPreferences
    fun getSeatStates(tripTitle: String, sharedPreferences: SharedPreferences): MutableMap<String, String> {
        val seatStates = mutableMapOf<String, String>()
        for (row in 'A'..'E') {
            for (col in 1..4) {
                val seatId = "$row$col"
                seatStates[seatId] = sharedPreferences.getString("$preferencesKeySeatStates$tripTitle$seatId", "Disponible")
                    ?: "Disponible"
            }
        }
        return seatStates
    }

    // Actualizar los estados de los asientos
    fun updateSeatStates(tripTitle: String, seatAssignments: MutableMap<String, String>, sharedPreferences: SharedPreferences) {
        val reservedSeatsCount = seatAssignments.values.count { it.startsWith("Ocupado") }

        val index = reservations.indexOfFirst { it.title == tripTitle }
        if (index >= 0) {
            reservations[index] = reservations[index].copy(
                reservedSeats = reservedSeatsCount,
                availableSeats = reservations[index].totalSeats - reservedSeatsCount,
                seatStates = seatAssignments
            )
            saveReservationToPreferences(sharedPreferences, reservations[index])
            Log.d("ReservationViewModel", "Estados de asientos actualizados: ${reservations[index]}")
        }
    }

    // Obtener asientos disponibles
    fun getAvailableSeats(title: String, zone: String): Int {
        return reservations.find { it.title == title && it.zone == zone }?.availableSeats ?: 0
    }

    // Obtener asientos reservados
    fun getReservedSeats(title: String, zone: String): Int {
        return reservations.find { it.title == title && it.zone == zone }?.reservedSeats ?: 0
    }
}

// Modelo de datos para una reserva
data class Reservation(
    val title: String,
    val zone: String,
    val totalSeats: Int,
    val reservedSeats: Int,
    val availableSeats: Int,
    val seatStates: MutableMap<String, String> = mutableMapOf()
)
