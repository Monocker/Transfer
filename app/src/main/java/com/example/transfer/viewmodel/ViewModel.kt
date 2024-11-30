package com.example.transfer.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.transfer.data.model.Reservation

class ReservationViewModel : ViewModel() {
    private val preferencesKeyReservedSeats = "reserved_seats_"
    private val preferencesKeyAvailableSeats = "available_seats_"
    private val preferencesKeySeatStates = "seat_states_"
    private val preferencesKeyReservations = "reservations_initialized"

    // Lista observable para que Compose detecte los cambios
    val reservations = mutableStateListOf<Reservation>()

    // Reserva actual en proceso
    var currentReservation by mutableStateOf<Reservation?>(null)

    // Lista de reservas confirmadas
    val confirmedReservations = mutableStateListOf<Reservation>()

    // Propiedad para indicar si la reserva ha sido finalizada
    var reservationFinalized by mutableStateOf(false)
        private set

    // Inicializar reservas desde SharedPreferences o valores predeterminados
    fun initializeReservations(context: Context) {
        val sharedPreferences = context.getSharedPreferences("ReservationPrefs", Context.MODE_PRIVATE)

        // Verificar si ya están inicializadas las reservas
        val isInitialized = sharedPreferences.getBoolean(preferencesKeyReservations, false)

        if (!isInitialized) {
            // Modelos y zonas para inicializar
            val models = listOf("Van", "Transit", "Rifter")
            val zones = listOf("Cancún", "Puerto Morelos", "Riviera Maya")

            val defaultReservations = mutableListOf<Reservation>()
            models.forEach { model ->
                zones.forEach { zone ->
                    val totalSeats = when (model) {
                        "Van" -> 15
                        "Transit" -> 12
                        "Rifter" -> 10
                        else -> 0
                    }
                    defaultReservations.add(
                        Reservation(
                            zone = zone,
                            model = model,
                            totalSeats = totalSeats,
                            reservedSeats = 0,
                            availableSeats = totalSeats,
                            seatStates = mutableMapOf()
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
            val keySuffix = "${reservation.model}_${reservation.zone}"
            editor.putInt("$preferencesKeyReservedSeats$keySuffix", reservation.reservedSeats)
            editor.putInt("$preferencesKeyAvailableSeats$keySuffix", reservation.availableSeats)
            // Guardar estados de los asientos serializados
            val serializedSeatStates = serializeSeatStates(reservation.seatStates)
            editor.putString("$preferencesKeySeatStates$keySuffix", serializedSeatStates)
        }
        editor.apply()
        Log.d("ReservationViewModel", "Reservas guardadas correctamente en SharedPreferences")
    }

    // Cargar todas las reservas desde SharedPreferences
    fun loadReservationsFromPreferences(sharedPreferences: SharedPreferences) {
        reservations.clear()
        val models = listOf("Van", "Transit", "Rifter")
        val zones = listOf("Cancún", "Puerto Morelos", "Riviera Maya")

        models.forEach { model ->
            zones.forEach { zone ->
                val keySuffix = "${model}_${zone}"
                val reservedSeats = sharedPreferences.getInt("$preferencesKeyReservedSeats$keySuffix", 0)
                val totalSeats = when (model) {
                    "Van" -> 15
                    "Transit" -> 12
                    "Rifter" -> 10
                    else -> 0
                }
                val availableSeats = sharedPreferences.getInt("$preferencesKeyAvailableSeats$keySuffix", totalSeats - reservedSeats)

                val seatStates = getSeatStates(model, zone, sharedPreferences)

                reservations.add(
                    Reservation(
                        zone = zone,
                        model = model,
                        totalSeats = totalSeats,
                        reservedSeats = reservedSeats,
                        availableSeats = availableSeats,
                        seatStates = seatStates
                    )
                )
            }
        }
        Log.d("ReservationViewModel", "Reservas cargadas correctamente: $reservations")
    }

    // Actualizar una reserva específica
    fun updateReservation(context: Context, model: String, zone: String, reservedSeats: Int) {
        val sharedPreferences = context.getSharedPreferences("ReservationPrefs", Context.MODE_PRIVATE)
        val index = reservations.indexOfFirst { it.model == model && it.zone == zone }

        if (index >= 0) {
            val totalSeats = reservations[index].totalSeats
            reservations[index] = reservations[index].copy(
                reservedSeats = reservedSeats,
                availableSeats = totalSeats - reservedSeats
            )
            saveReservationToPreferences(sharedPreferences, reservations[index])
            Log.d("ReservationViewModel", "Reserva actualizada: ${reservations[index]}")
        } else {
            Log.e("ReservationViewModel", "No se encontró reserva para $model en $zone")
        }
    }

    // Guardar una reserva específica en SharedPreferences
    private fun saveReservationToPreferences(sharedPreferences: SharedPreferences, reservation: Reservation) {
        val editor = sharedPreferences.edit()
        val keySuffix = "${reservation.model}_${reservation.zone}"
        editor.putInt("$preferencesKeyReservedSeats$keySuffix", reservation.reservedSeats)
        editor.putInt("$preferencesKeyAvailableSeats$keySuffix", reservation.availableSeats)
        // Guardar estados de los asientos serializados
        val serializedSeatStates = serializeSeatStates(reservation.seatStates)
        editor.putString("$preferencesKeySeatStates$keySuffix", serializedSeatStates)
        editor.apply()
    }

    // Obtener estados de los asientos desde SharedPreferences
    fun getSeatStates(model: String, zone: String, sharedPreferences: SharedPreferences): MutableMap<String, String> {
        val keySuffix = "${model}_${zone}"
        val serializedSeatStates = sharedPreferences.getString("$preferencesKeySeatStates$keySuffix", null)
        return if (serializedSeatStates != null) {
            deserializeSeatStates(serializedSeatStates)
        } else {
            mutableMapOf()
        }
    }

    // Actualizar los estados de los asientos
    fun updateSeatStates(model: String, zone: String, seatAssignments: MutableMap<String, String>, sharedPreferences: SharedPreferences) {
        val reservedSeatsCount = seatAssignments.values.count { it.startsWith("Ocupado") }

        val index = reservations.indexOfFirst { it.model == model && it.zone == zone }
        if (index >= 0) {
            val totalSeats = reservations[index].totalSeats
            reservations[index] = reservations[index].copy(
                reservedSeats = reservedSeatsCount,
                availableSeats = totalSeats - reservedSeatsCount,
                seatStates = seatAssignments
            )
            saveReservationToPreferences(sharedPreferences, reservations[index])
            Log.d("ReservationViewModel", "Estados de asientos actualizados: ${reservations[index]}")
        }
    }

    // Serializar estados de los asientos sin Gson
    private fun serializeSeatStates(seatStates: MutableMap<String, String>): String {
        return seatStates.entries.joinToString(";") { "${it.key},${it.value}" }
    }

    // Deserializar estados de los asientos sin Gson
    private fun deserializeSeatStates(serializedSeatStates: String): MutableMap<String, String> {
        val seatStates = mutableMapOf<String, String>()
        if (serializedSeatStates.isNotEmpty()) {
            val entries = serializedSeatStates.split(";")
            entries.forEach { entry ->
                val parts = entry.split(",")
                if (parts.size == 2) {
                    val key = parts[0]
                    val value = parts[1]
                    seatStates[key] = value
                }
            }
        }
        return seatStates
    }

    // Obtener asientos disponibles
    fun getAvailableSeats(model: String, zone: String): Int {
        return reservations.find { it.model == model && it.zone == zone }?.availableSeats ?: 0
    }

    // Obtener asientos reservados
    fun getReservedSeats(model: String, zone: String): Int {
        return reservations.find { it.model == model && it.zone == zone }?.reservedSeats ?: 0
    }

    // Agregar una reserva confirmada
    fun addConfirmedReservation(reservation: Reservation) {
        confirmedReservations.add(reservation)
        // Aquí puedes guardar la reserva en SharedPreferences o en una base de datos si lo deseas
        Log.d("ReservationViewModel", "Reserva confirmada añadida: $reservation")
    }

    // Función para finalizar la reserva
    fun finalizeReservation() {
        reservationFinalized = true
    }

    // Función para resetear el estado de finalización de la reserva
    fun resetReservationFinalized() {
        reservationFinalized = false
    }
}
