package com.example.gasolinerav3.datos

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatosHelper(context: Context) :
    SQLiteOpenHelper(context, "gasolinera.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val crearTipoCombustible = """
            CREATE TABLE IF NOT EXISTS TipoCombustible (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT UNIQUE NOT NULL
            );
        """.trimIndent()

        val crearEstacion = """
            CREATE TABLE IF NOT EXISTS Estacion (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                direccion TEXT,
                latitud REAL NOT NULL,
                longitud REAL NOT NULL
            );
        """.trimIndent()

        val crearBomba = """
            CREATE TABLE IF NOT EXISTS Bomba (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                estacionId INTEGER NOT NULL,
                tipoId INTEGER NOT NULL,
                cantidad INTEGER NOT NULL,
                FOREIGN KEY (estacionId) REFERENCES Estacion(id),
                FOREIGN KEY (tipoId) REFERENCES TipoCombustible(id)
            );
        """.trimIndent()

        val crearStockCombustible = """
            CREATE TABLE IF NOT EXISTS StockCombustible (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                estacionId INTEGER NOT NULL,
                tipoId INTEGER NOT NULL,
                litrosDisponibles REAL NOT NULL,
                fechaActualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (estacionId) REFERENCES Estacion(id),
                FOREIGN KEY (tipoId) REFERENCES TipoCombustible(id)
            );
        """.trimIndent()

        val crearFila = """
            CREATE TABLE IF NOT EXISTS Fila (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                estacionId INTEGER NOT NULL,
                tipoId INTEGER NOT NULL,
                tiempoEstimado INTEGER NOT NULL,
                alcanzaCombustible BOOLEAN NOT NULL,
                fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (estacionId) REFERENCES Estacion(id),
                FOREIGN KEY (tipoId) REFERENCES TipoCombustible(id)
            );
        """.trimIndent()

        // Ejecutar todas las tablas
        db.execSQL(crearTipoCombustible)
        db.execSQL(crearEstacion)
        db.execSQL(crearBomba)
        db.execSQL(crearStockCombustible)
        db.execSQL(crearFila)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Fila")
        db.execSQL("DROP TABLE IF EXISTS StockCombustible")
        db.execSQL("DROP TABLE IF EXISTS Bomba")
        db.execSQL("DROP TABLE IF EXISTS Estacion")
        db.execSQL("DROP TABLE IF EXISTS TipoCombustible")
        onCreate(db)
    }
}
