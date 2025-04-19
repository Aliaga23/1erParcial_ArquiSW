package com.example.gasolinerav3.datos

import android.content.ContentValues
import android.content.Context

data class Fila(
    val id: Int = 0,
    val estacionId: Int,
    val tipoId: Int,
    val tiempoEstimado: Int,
    val alcanzaCombustible: Boolean,
    val fecha: String = ""
)

class DFila(context: Context) {
    private val dbHelper = BaseDatosHelper(context)

    fun insertar(fila: Fila): Boolean {
        val valores = ContentValues().apply {
            put("estacionId", fila.estacionId)
            put("tipoId", fila.tipoId)
            put("tiempoEstimado", fila.tiempoEstimado)
            put("alcanzaCombustible", if (fila.alcanzaCombustible) 1 else 0)
        }
        return dbHelper.writableDatabase.insert("Fila", null, valores) > 0
    }

    fun listar(): List<Fila> {
        val lista = mutableListOf<Fila>()
        val cursor = dbHelper.readableDatabase.rawQuery("SELECT * FROM Fila ORDER BY id DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val estacionId = cursor.getInt(cursor.getColumnIndexOrThrow("estacionId"))
                val tipoId = cursor.getInt(cursor.getColumnIndexOrThrow("tipoId"))
                val tiempo = cursor.getInt(cursor.getColumnIndexOrThrow("tiempoEstimado"))
                val alcanza = cursor.getInt(cursor.getColumnIndexOrThrow("alcanzaCombustible")) == 1
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
                lista.add(Fila(id, estacionId, tipoId, tiempo, alcanza, fecha))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }
    fun obtenerCantidadBombas(estacionId: Int, tipoId: Int): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT cantidad FROM Bomba WHERE estacionId = ? AND tipoId = ?",
            arrayOf(estacionId.toString(), tipoId.toString())
        )
        var total = 0
        if (cursor.moveToFirst()) {
            do {
                total += cursor.getInt(0)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return total
    }

}
