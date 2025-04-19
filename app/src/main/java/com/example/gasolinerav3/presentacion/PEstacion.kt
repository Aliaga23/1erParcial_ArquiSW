package com.example.gasolinerav3.presentacion

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.example.gasolinerav3.R
import com.example.gasolinerav3.datos.Estacion
import com.example.gasolinerav3.negocio.NEstacion
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions

class PEstacion : Fragment() {

    private lateinit var nEstacion: NEstacion
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdaptadorEstacion

    private lateinit var inputNombre: EditText
    private lateinit var inputDireccion: EditText
    private lateinit var inputLatitud: EditText
    private lateinit var inputLongitud: EditText
    private lateinit var btnAccion: Button
    private lateinit var btnMostrarMapa: Button
    private lateinit var mapContainer: FrameLayout

    private var modoEditar = false
    private var idEditando = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val vista = inflater.inflate(R.layout.fragment_estacion, container, false)

        nEstacion = NEstacion(requireContext())

        inputNombre = vista.findViewById(R.id.inputNombre)
        inputDireccion = vista.findViewById(R.id.inputDireccion)
        inputLatitud = vista.findViewById(R.id.inputLatitud)
        inputLongitud = vista.findViewById(R.id.inputLongitud)
        btnAccion = vista.findViewById(R.id.btnAgregar)
        btnMostrarMapa = vista.findViewById(R.id.btnMostrarMapa)
        mapContainer = vista.findViewById(R.id.mapContainer)

        recyclerView = vista.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = AdaptadorEstacion(
            onEditar = { estacion ->
                inputNombre.setText(estacion.nombre)
                inputDireccion.setText(estacion.direccion)
                inputLatitud.setText(estacion.latitud.toString())
                inputLongitud.setText(estacion.longitud.toString())
                idEditando = estacion.id
                modoEditar = true
                btnAccion.text = "Actualizar"
            },
            onEliminar = { estacion ->
                nEstacion.eliminar(estacion.id)
                cargarDatos()
                Toast.makeText(context, "Estación eliminada", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = adapter

        btnMostrarMapa.setOnClickListener {
            mapContainer.visibility = View.VISIBLE
            val mapFragment = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction()
                .replace(R.id.mapContainer, mapFragment)
                .commit()

            mapFragment.getMapAsync { googleMap ->
                googleMap.uiSettings.isZoomControlsEnabled = true
                googleMap.setOnMapClickListener { latLng ->
                    inputLatitud.setText(latLng.latitude.toString())
                    inputLongitud.setText(latLng.longitude.toString())

                    googleMap.clear()
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title("Ubicación seleccionada")
                    )
                }
            }
        }

        btnAccion.setOnClickListener {
            val nombre = inputNombre.text.toString()
            val direccion = inputDireccion.text.toString()
            val latitud = inputLatitud.text.toString().toDoubleOrNull()
            val longitud = inputLongitud.text.toString().toDoubleOrNull()

            if (nombre.isBlank() || direccion.isBlank() || latitud == null || longitud == null) {
                Toast.makeText(context, "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val estacion = Estacion(
                id = idEditando,
                nombre = nombre,
                direccion = direccion,
                latitud = latitud,
                longitud = longitud
            )

            if (modoEditar) {
                nEstacion.editar(estacion)
                Toast.makeText(context, "Estación actualizada", Toast.LENGTH_SHORT).show()
            } else {
                nEstacion.crear(estacion)
                Toast.makeText(context, "Estación agregada", Toast.LENGTH_SHORT).show()
            }

            limpiarCampos()
            cargarDatos()
        }

        cargarDatos()
        return vista
    }

    private fun limpiarCampos() {
        inputNombre.text.clear()
        inputDireccion.text.clear()
        inputLatitud.text.clear()
        inputLongitud.text.clear()
        modoEditar = false
        idEditando = -1
        btnAccion.text = "Agregar"
    }

    private fun cargarDatos() {
        adapter.actualizar(nEstacion.listar())
    }

    class AdaptadorEstacion(
        val onEditar: (Estacion) -> Unit,
        val onEliminar: (Estacion) -> Unit
    ) : RecyclerView.Adapter<AdaptadorEstacion.ViewHolder>() {

        private var lista = listOf<Estacion>()

        fun actualizar(nuevaLista: List<Estacion>) {
            lista = nuevaLista
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val vista = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_estacion, parent, false)
            return ViewHolder(vista)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val estacion = lista[position]
            holder.txtNombre.text = estacion.nombre
            holder.txtDireccion.text = estacion.direccion
            holder.txtCoord.text = "Lat: ${estacion.latitud}, Lng: ${estacion.longitud}"
            holder.btnEditar.setOnClickListener { onEditar(estacion) }
            holder.btnEliminar.setOnClickListener { onEliminar(estacion) }
        }

        override fun getItemCount() = lista.size

        class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
            val txtNombre: TextView = vista.findViewById(R.id.txtNombre)
            val txtDireccion: TextView = vista.findViewById(R.id.txtDireccion)
            val txtCoord: TextView = vista.findViewById(R.id.txtCoord)
            val btnEditar: Button = vista.findViewById(R.id.btnEditar)
            val btnEliminar: Button = vista.findViewById(R.id.btnEliminar)
        }
    }
}
