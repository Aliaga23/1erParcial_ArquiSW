package com.example.gasolinerav3.presentacion

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.example.gasolinerav3.R
import com.example.gasolinerav3.datos.*
import com.example.gasolinerav3.negocio.*

class PStockCombustible : Fragment() {

    private lateinit var nStock: NStockCombustible
    private lateinit var nEstacion: NEstacion
    private lateinit var nTipo: NTipoCombustible

    private lateinit var spinnerEstacion: Spinner
    private lateinit var spinnerTipo: Spinner
    private lateinit var inputLitros: EditText
    private lateinit var btnAccion: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdaptadorStock

    private var idEditando = -1
    private var modoEditar = false
    private var listaEstaciones = listOf<Estacion>()
    private var listaTipos = listOf<TipoCombustible>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val vista = inflater.inflate(R.layout.fragment_stock_combustible, container, false)

        nStock = NStockCombustible(requireContext())
        nEstacion = NEstacion(requireContext())
        nTipo = NTipoCombustible(requireContext())

        spinnerEstacion = vista.findViewById(R.id.spinnerEstacionStock)
        spinnerTipo = vista.findViewById(R.id.spinnerTipoStock)
        inputLitros = vista.findViewById(R.id.inputLitros)
        btnAccion = vista.findViewById(R.id.btnAgregarStock)
        recyclerView = vista.findViewById(R.id.recyclerViewStock)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AdaptadorStock(
            onEditar = { stock ->
                inputLitros.setText(stock.litrosDisponibles.toString())
                spinnerEstacion.setSelection(listaEstaciones.indexOfFirst { it.id == stock.estacionId })
                spinnerTipo.setSelection(listaTipos.indexOfFirst { it.id == stock.tipoId })
                idEditando = stock.id
                modoEditar = true
                btnAccion.text = "Actualizar"
            },
            onEliminar = { stock ->
                nStock.eliminar(stock.id)
                cargarStock()
                Toast.makeText(context, "Stock eliminado", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = adapter

        btnAccion.setOnClickListener {
            val estacion = listaEstaciones.getOrNull(spinnerEstacion.selectedItemPosition)
            val tipo = listaTipos.getOrNull(spinnerTipo.selectedItemPosition)
            val litros = inputLitros.text.toString().toDoubleOrNull()

            if (estacion == null || tipo == null || litros == null || litros <= 0) {
                Toast.makeText(context, "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val stock = StockCombustible(
                id = idEditando,
                estacionId = estacion.id,
                tipoId = tipo.id,
                litrosDisponibles = litros
            )

            if (modoEditar) {
                nStock.editar(stock)
                Toast.makeText(context, "Stock actualizado", Toast.LENGTH_SHORT).show()
            } else {
                nStock.crear(stock)
                Toast.makeText(context, "Stock registrado", Toast.LENGTH_SHORT).show()
            }

            limpiar()
            cargarStock()
        }

        cargarSpinners()
        cargarStock()
        return vista
    }

    private fun cargarSpinners() {
        listaEstaciones = nEstacion.listar()
        listaTipos = nTipo.listar()

        spinnerEstacion.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listaEstaciones.map { it.nombre }
        )

        spinnerTipo.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listaTipos.map { it.nombre }
        )
    }

    private fun cargarStock() {
        adapter.actualizar(nStock.listar(), listaEstaciones, listaTipos)
    }

    private fun limpiar() {
        inputLitros.text.clear()
        spinnerEstacion.setSelection(0)
        spinnerTipo.setSelection(0)
        idEditando = -1
        modoEditar = false
        btnAccion.text = "Agregar"
    }

    class AdaptadorStock(
        val onEditar: (StockCombustible) -> Unit,
        val onEliminar: (StockCombustible) -> Unit
    ) : RecyclerView.Adapter<AdaptadorStock.ViewHolder>() {

        private var lista = listOf<StockCombustible>()
        private var estaciones = listOf<Estacion>()
        private var tipos = listOf<TipoCombustible>()

        fun actualizar(nuevaLista: List<StockCombustible>, est: List<Estacion>, tip: List<TipoCombustible>) {
            lista = nuevaLista
            estaciones = est
            tipos = tip
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val vista = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_stock_combustible, parent, false)
            return ViewHolder(vista)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val stock = lista[position]
            val estacion = estaciones.find { it.id == stock.estacionId }?.nombre ?: "Desconocida"
            val tipo = tipos.find { it.id == stock.tipoId }?.nombre ?: "Desconocido"
            holder.txtInfo.text = "Estación: $estacion\nTipo: $tipo\nLitros: ${stock.litrosDisponibles} L\nFecha: ${stock.fecha}"
            holder.btnEditar.setOnClickListener { onEditar(stock) }
            holder.btnEliminar.setOnClickListener { onEliminar(stock) }
        }

        override fun getItemCount() = lista.size

        class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
            val txtInfo: TextView = vista.findViewById(R.id.txtInfoStock)
            val btnEditar: Button = vista.findViewById(R.id.btnEditarStock)
            val btnEliminar: Button = vista.findViewById(R.id.btnEliminarStock)
        }
    }
}
