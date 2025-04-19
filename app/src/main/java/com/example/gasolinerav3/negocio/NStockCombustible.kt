package com.example.gasolinerav3.negocio

import android.content.Context
import com.example.gasolinerav3.datos.DStockCombustible
import com.example.gasolinerav3.datos.StockCombustible

class NStockCombustible(context: Context) {
    private val dStock = DStockCombustible(context)

    fun listar(): List<StockCombustible> = dStock.listar()

    fun crear(stock: StockCombustible): Boolean = dStock.insertar(stock)

    fun editar(stock: StockCombustible): Boolean = dStock.actualizar(stock)

    fun eliminar(id: Int): Boolean = dStock.eliminar(id)
}
