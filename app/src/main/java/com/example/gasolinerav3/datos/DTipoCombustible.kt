package com.example.gasolinerav3.datos

import android.content.ContentValues
import android.content.Context

data class TipoCombustible(val id: Int = 0, val nombre: String)

class DTipoCombustible(context: Context) {
    private val dbHelper = BaseDatosHelper(context)

    fun insertar(nombre: String): Boolean {
        val valores = ContentValues().apply { put("nombre", nombre) }
        return dbHelper.writableDatabase.insert("TipoCombustible", null, valores) > 0
    }

    fun actualizar(id: Int, nombre: String): Boolean {
        val valores = ContentValues().apply { put("nombre", nombre) }
        return dbHelper.writableDatabase.update("TipoCombustible", valores, "id=?", arrayOf(id.toString())) > 0
    }

    fun eliminar(id: Int): Boolean {
        return dbHelper.writableDatabase.delete("TipoCombustible", "id=?", arrayOf(id.toString())) > 0
    }

    fun listar(): List<TipoCombustible> {
        val lista = mutableListOf<TipoCombustible>()
        val cursor = dbHelper.readableDatabase.rawQuery("SELECT * FROM TipoCombustible ORDER BY id", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                lista.add(TipoCombustible(id, nombre))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }
}
