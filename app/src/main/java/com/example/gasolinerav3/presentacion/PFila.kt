package com.example.gasolinerav3.presentacion

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.gasolinerav3.R
import com.example.gasolinerav3.datos.*
import com.example.gasolinerav3.negocio.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class PFila : Fragment() {

    private lateinit var spinnerTipo: Spinner
    private lateinit var spinnerEstacion: Spinner
    private lateinit var txtStock: TextView
    private lateinit var btnConfirmar: Button
    private lateinit var btnReiniciarTrazo: Button
    private lateinit var resumen: TextView

    private lateinit var nTipo: NTipoCombustible
    private lateinit var nEstacion: NEstacion
    private lateinit var nStock: NStockCombustible
    private lateinit var nFila: NFila

    private lateinit var listaTipos: List<TipoCombustible>
    private var estacionesFiltradas: List<Estacion> = listOf()
    private lateinit var listaStock: List<StockCombustible>

    private var puntosDibujo = mutableListOf<LatLng>()
    private var googleMap: GoogleMap? = null
    private var marcadorEstacion: Marker? = null
    private var polylineFila: Polyline? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val vista = inflater.inflate(R.layout.fragment_fila, container, false)

        spinnerTipo = vista.findViewById(R.id.spinnerTipoFila)
        spinnerEstacion = vista.findViewById(R.id.spinnerEstacionFila)
        txtStock = vista.findViewById(R.id.txtStockDisponible)
        btnConfirmar = vista.findViewById(R.id.btnConfirmarFila)
        btnReiniciarTrazo = vista.findViewById(R.id.btnReiniciarTrazo)
        resumen = vista.findViewById(R.id.txtResumen)

        nTipo = NTipoCombustible(requireContext())
        nEstacion = NEstacion(requireContext())
        nStock = NStockCombustible(requireContext())
        nFila = NFila(requireContext())

        listaTipos = nTipo.listar()
        listaStock = nStock.listar()

        spinnerTipo.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listaTipos.map { it.nombre })

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFila) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it
            it.uiSettings.isZoomControlsEnabled = true
            it.setOnMapClickListener { latLng ->
                puntosDibujo.add(latLng)
                it.addMarker(MarkerOptions().position(latLng))
                polylineFila?.remove()
                polylineFila = it.addPolyline(
                    PolylineOptions().addAll(puntosDibujo).color(0xFF0000FF.toInt())
                )
            }
        }

        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                val tipoSeleccionado = listaTipos.getOrNull(position)
                if (tipoSeleccionado != null) {
                    estacionesFiltradas = nEstacion.listar().filter { estacion ->
                        listaStock.any { it.estacionId == estacion.id && it.tipoId == tipoSeleccionado.id }
                    }
                    spinnerEstacion.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, estacionesFiltradas.map { it.nombre })
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        spinnerEstacion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                actualizarStock()
                mostrarEstacionEnMapa()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        btnConfirmar.setOnClickListener {
            if (puntosDibujo.size < 2) {
                Toast.makeText(context, "Dibuja una fila en el mapa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tipo = listaTipos.getOrNull(spinnerTipo.selectedItemPosition)
            val estacion = estacionesFiltradas.getOrNull(spinnerEstacion.selectedItemPosition)
            val stock = obtenerStockDisponible(estacion?.id ?: 0, tipo?.id ?: 0)

            if (tipo == null || estacion == null || stock == null) {
                Toast.makeText(context, "Datos inválidos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val distancia = calcularLongitudEnMetros(puntosDibujo)
            val resultado = nFila.calcularEstimacion(distancia, stock, estacion.id, tipo.id)

            val litros = resultado["litrosNecesarios"] as Double
            val alcanza = resultado["alcanza"] as Boolean
            val tiempo = resultado["tiempoEstimado"] as Int

            resumen.text = """
                Litros necesarios: $litros L
                ¿Alcanza?: ${if (alcanza) "Sí ✅" else "No ❌"}
                Tiempo estimado: $tiempo min
            """.trimIndent()
        }

        btnReiniciarTrazo.setOnClickListener {
            puntosDibujo.clear()
            polylineFila?.remove()
            googleMap?.clear()
            marcadorEstacion?.let {
                marcadorEstacion = googleMap?.addMarker(
                    MarkerOptions()
                        .position(it.position)
                        .title(it.title)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                )
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(it.position, 17f))
            }
            resumen.text = ""
        }

        return vista
    }

    private fun actualizarStock() {
        val tipo = listaTipos.getOrNull(spinnerTipo.selectedItemPosition)
        val estacion = estacionesFiltradas.getOrNull(spinnerEstacion.selectedItemPosition)
        val stock = obtenerStockDisponible(estacion?.id ?: 0, tipo?.id ?: 0)
        txtStock.text = "Stock disponible: ${stock ?: "N/D"} litros"
    }

    private fun obtenerStockDisponible(estacionId: Int, tipoId: Int): Double? {
        return listaStock.find { it.estacionId == estacionId && it.tipoId == tipoId }?.litrosDisponibles
    }

    private fun mostrarEstacionEnMapa() {
        val estacion = estacionesFiltradas.getOrNull(spinnerEstacion.selectedItemPosition) ?: return
        val ubicacion = LatLng(estacion.latitud, estacion.longitud)

        googleMap?.apply {
            marcadorEstacion?.remove()
            marcadorEstacion = addMarker(
                MarkerOptions()
                    .position(ubicacion)
                    .title(estacion.nombre)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )
            animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 17f))
        }
    }

    private fun calcularLongitudEnMetros(puntos: List<LatLng>): Double {
        var distancia = 0.0
        for (i in 1 until puntos.size) {
            val res = FloatArray(1)
            val p1 = puntos[i - 1]
            val p2 = puntos[i]
            android.location.Location.distanceBetween(
                p1.latitude, p1.longitude,
                p2.latitude, p2.longitude,
                res
            )
            distancia += res[0]
        }
        return distancia
    }
}
