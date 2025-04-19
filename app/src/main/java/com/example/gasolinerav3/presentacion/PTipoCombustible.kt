package com.example.gasolinerav3.presentacion

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.example.gasolinerav3.R
import com.example.gasolinerav3.datos.TipoCombustible
import com.example.gasolinerav3.negocio.NTipoCombustible

class PTipoCombustible : Fragment() {

    private lateinit var nTipo: NTipoCombustible
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdaptadorTipo
    private lateinit var input: EditText
    private lateinit var btnAccion: Button

    private var modoEditar = false
    private var idEditando = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val vista = inflater.inflate(R.layout.fragment_tipo_combustible, container, false)

        nTipo = NTipoCombustible(requireContext())

        recyclerView = vista.findViewById(R.id.recyclerView)
        input = vista.findViewById(R.id.inputNombre)
        btnAccion = vista.findViewById(R.id.btnAgregar)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AdaptadorTipo(
            onEditar = { tipo ->
                input.setText(tipo.nombre)
                idEditando = tipo.id
                modoEditar = true
                btnAccion.text = "Actualizar"
            },
            onEliminar = { tipo ->
                nTipo.eliminar(tipo.id)
                cargarDatos()
                Toast.makeText(context, "Eliminado", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = adapter

        btnAccion.setOnClickListener {
            val nombre = input.text.toString()
            if (nombre.isNotEmpty()) {
                if (modoEditar) {
                    nTipo.editar(idEditando, nombre)
                    Toast.makeText(context, "Actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    nTipo.crear(nombre)
                    Toast.makeText(context, "Agregado", Toast.LENGTH_SHORT).show()
                }
                input.text.clear()
                btnAccion.text = "Agregar"
                modoEditar = false
                idEditando = -1
                cargarDatos()
            } else {
                Toast.makeText(context, "Nombre vacío", Toast.LENGTH_SHORT).show()
            }
        }

        cargarDatos()
        return vista
    }

    private fun cargarDatos() {
        adapter.actualizar(nTipo.listar())
    }

    class AdaptadorTipo(
        val onEditar: (TipoCombustible) -> Unit,
        val onEliminar: (TipoCombustible) -> Unit
    ) : RecyclerView.Adapter<AdaptadorTipo.ViewHolder>() {

        private var lista = listOf<TipoCombustible>()

        fun actualizar(nuevaLista: List<TipoCombustible>) {
            lista = nuevaLista
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val vista = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_tipo_combustible, parent, false)
            return ViewHolder(vista)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val tipo = lista[position]
            holder.nombre.text = tipo.nombre
            holder.btnEditar.setOnClickListener { onEditar(tipo) }
            holder.btnEliminar.setOnClickListener { onEliminar(tipo) }
        }

        override fun getItemCount() = lista.size

        class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
            val nombre: TextView = vista.findViewById(R.id.txtNombre)
            val btnEditar: Button = vista.findViewById(R.id.btnEditar)
            val btnEliminar: Button = vista.findViewById(R.id.btnEliminar)
        }
    }
}
