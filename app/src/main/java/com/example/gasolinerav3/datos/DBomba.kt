package com.example.gasolinerav3.datos

import android.content.ContentValues
import android.content.Context

data class Bomba(
    val id: Int = 0,
    val estacionId: Int,
    val tipoId: Int,
    val cantidad: Int
)

class DBomba(context: Context) {
    private val dbHelper = BaseDatosHelper(context)

    fun insertar(bomba: Bomba): Boolean {
        val valores = ContentValues().apply {
            put("estacionId", bomba.estacionId)
            put("tipoId", bomba.tipoId)
            put("cantidad", bomba.cantidad)
        }
        return dbHelper.writableDatabase.insert("Bomba", null, valores) > 0
    }

    fun actualizar(bomba: Bomba): Boolean {
        val valores = ContentValues().apply {
            put("estacionId", bomba.estacionId)
            put("tipoId", bomba.tipoId)
            put("cantidad", bomba.cantidad)
        }
        return dbHelper.writableDatabase.update(
            "Bomba", valores, "id=?", arrayOf(bomba.id.toString())
        ) > 0
    }

    fun eliminar(id: Int): Boolean {
        return dbHelper.writableDatabase.delete("Bomba", "id=?", arrayOf(id.toString())) > 0
    }

    fun listar(): List<Bomba> {
        val lista = mutableListOf<Bomba>()
        val cursor = dbHelper.readableDatabase.rawQuery("SELECT * FROM Bomba ORDER BY id", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val estacionId = cursor.getInt(cursor.getColumnIndexOrThrow("estacionId"))
                val tipoId = cursor.getInt(cursor.getColumnIndexOrThrow("tipoId"))
                val cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))
                lista.add(Bomba(id, estacionId, tipoId, cantidad))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }
}
