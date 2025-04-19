package com.example.gasolinerav3.presentacion

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.example.gasolinerav3.R
import com.example.gasolinerav3.datos.Bomba
import com.example.gasolinerav3.datos.Estacion
import com.example.gasolinerav3.datos.TipoCombustible
import com.example.gasolinerav3.negocio.NBomba
import com.example.gasolinerav3.negocio.NEstacion
import com.example.gasolinerav3.negocio.NTipoCombustible

class PBomba : Fragment() {

    private lateinit var nBomba: NBomba
    private lateinit var nEstacion: NEstacion
    private lateinit var nTipo: NTipoCombustible

    private lateinit var spinnerEstacion: Spinner
    private lateinit var spinnerTipo: Spinner
    private lateinit var inputCantidad: EditText
    private lateinit var btnAccion: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdaptadorBomba

    private var idEditando = -1
    private var modoEditar = false
    private var listaEstaciones = listOf<Estacion>()
    private var listaTipos = listOf<TipoCombustible>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val vista = inflater.inflate(R.layout.fragment_bomba, container, false)

        nBomba = NBomba(requireContext())
        nEstacion = NEstacion(requireContext())
        nTipo = NTipoCombustible(requireContext())

        spinnerEstacion = vista.findViewById(R.id.spinnerEstacion)
        spinnerTipo = vista.findViewById(R.id.spinnerTipo)
        inputCantidad = vista.findViewById(R.id.inputCantidad)
        btnAccion = vista.findViewById(R.id.btnAgregar)
        recyclerView = vista.findViewById(R.id.recyclerViewBomba)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AdaptadorBomba(
            onEditar = { bomba ->
                val estacionIndex = listaEstaciones.indexOfFirst { it.id == bomba.estacionId }
                val tipoIndex = listaTipos.indexOfFirst { it.id == bomba.tipoId }

                spinnerEstacion.setSelection(estacionIndex)
                spinnerTipo.setSelection(tipoIndex)
                inputCantidad.setText(bomba.cantidad.toString())

                idEditando = bomba.id
                modoEditar = true
                btnAccion.text = "Actualizar"
            },
            onEliminar = { bomba ->
                nBomba.eliminar(bomba.id)
                cargarBombas()
                Toast.makeText(context, "Bomba eliminada", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = adapter

        btnAccion.setOnClickListener {
            val estacion = listaEstaciones.getOrNull(spinnerEstacion.selectedItemPosition)
            val tipo = listaTipos.getOrNull(spinnerTipo.selectedItemPosition)
            val cantidad = inputCantidad.text.toString().toIntOrNull()

            if (estacion == null || tipo == null || cantidad == null || cantidad <= 0) {
                Toast.makeText(context, "Completa los datos correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bomba = Bomba(
                id = idEditando,
                estacionId = estacion.id,
                tipoId = tipo.id,
                cantidad = cantidad
            )

            if (modoEditar) {
                nBomba.editar(bomba)
                Toast.makeText(context, "Bomba actualizada", Toast.LENGTH_SHORT).show()
            } else {
                nBomba.crear(bomba)
                Toast.makeText(context, "Bomba registrada", Toast.LENGTH_SHORT).show()
            }

            limpiar()
            cargarBombas()
        }

        cargarSpinners()
        cargarBombas()

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

    private fun cargarBombas() {
        adapter.actualizar(nBomba.listar())
    }

    private fun limpiar() {
        inputCantidad.text.clear()
        spinnerEstacion.setSelection(0)
        spinnerTipo.setSelection(0)
        idEditando = -1
        modoEditar = false
        btnAccion.text = "Agregar"
    }

    class AdaptadorBomba(
        val onEditar: (Bomba) -> Unit,
        val onEliminar: (Bomba) -> Unit
    ) : RecyclerView.Adapter<AdaptadorBomba.ViewHolder>() {

        private var lista = listOf<Bomba>()

        fun actualizar(nuevaLista: List<Bomba>) {
            lista = nuevaLista
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val vista = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bomba, parent, false)
            return ViewHolder(vista)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val bomba = lista[position]
            holder.txtInfo.text = "ID Estación: ${bomba.estacionId}, Tipo: ${bomba.tipoId}, Cantidad: ${bomba.cantidad}"
            holder.btnEditar.setOnClickListener { onEditar(bomba) }
            holder.btnEliminar.setOnClickListener { onEliminar(bomba) }
        }

        override fun getItemCount() = lista.size

        class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
            val txtInfo: TextView = vista.findViewById(R.id.txtInfo)
            val btnEditar: Button = vista.findViewById(R.id.btnEditar)
            val btnEliminar: Button = vista.findViewById(R.id.btnEliminar)
        }
    }
}
