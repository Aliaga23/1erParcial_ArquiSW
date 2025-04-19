package com.example.gasolinerav3.negocio

import android.content.Context
import com.example.gasolinerav3.datos.DEstacion
import com.example.gasolinerav3.datos.Estacion

class NEstacion(context: Context) {
    private val dEstacion = DEstacion(context)

    fun listar(): List<Estacion> = dEstacion.listar()

    fun crear(estacion: Estacion): Boolean = dEstacion.insertar(estacion)

    fun editar(estacion: Estacion): Boolean = dEstacion.actualizar(estacion)

    fun eliminar(id: Int): Boolean = dEstacion.eliminar(id)
}
