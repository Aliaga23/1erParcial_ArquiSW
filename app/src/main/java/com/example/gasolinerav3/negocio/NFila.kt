package com.example.gasolinerav3.negocio

import android.content.Context
import com.example.gasolinerav3.datos.DFila
import com.example.gasolinerav3.datos.Fila

class NFila(context: Context) {
    private val dFila = DFila(context)

    fun calcularEstimacion(
        longitudMetros: Double,
        stockDisponible: Double,
        estacionId: Int,
        tipoId: Int,
        litrosPorAuto: Double = 30.0,
        largoAuto: Double = 5.0,
        minutosPorAuto: Int = 3
    ): Map<String, Any> {
        val autosEstimados = (longitudMetros / largoAuto).toInt()
        val litrosNecesarios = autosEstimados * litrosPorAuto
        val alcanza = litrosNecesarios <= stockDisponible

        val cantidadBombas = dFila.obtenerCantidadBombas(estacionId, tipoId).coerceAtLeast(1) // evitar división por 0

        val tiempoEstimado = (autosEstimados * minutosPorAuto) / cantidadBombas

        // Registrar fila
        val fila = Fila(
            estacionId = estacionId,
            tipoId = tipoId,
            tiempoEstimado = tiempoEstimado,
            alcanzaCombustible = alcanza
        )
        dFila.insertar(fila)

        return mapOf(
            "autos" to autosEstimados,
            "litrosNecesarios" to litrosNecesarios,
            "alcanza" to alcanza,
            "tiempoEstimado" to tiempoEstimado
        )
    }


    fun listar(): List<Fila> = dFila.listar()
}
