package com.example.gasolinerav3.negocio

import android.content.Context
import com.example.gasolinerav3.datos.Bomba
import com.example.gasolinerav3.datos.DBomba

class NBomba(context: Context) {
    private val dBomba = DBomba(context)

    fun listar(): List<Bomba> = dBomba.listar()

    fun crear(bomba: Bomba): Boolean = dBomba.insertar(bomba)

    fun editar(bomba: Bomba): Boolean = dBomba.actualizar(bomba)

    fun eliminar(id: Int): Boolean = dBomba.eliminar(id)
}
