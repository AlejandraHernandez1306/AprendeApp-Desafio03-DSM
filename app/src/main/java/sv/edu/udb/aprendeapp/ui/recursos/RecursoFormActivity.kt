package sv.edu.udb.aprendeapp.ui.recursos

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import sv.edu.udb.aprendeapp.R
import sv.edu.udb.aprendeapp.databinding.ActivityRecursoFormBinding
import sv.edu.udb.aprendeapp.model.Recurso
import sv.edu.udb.aprendeapp.repository.RecursoRepository
import sv.edu.udb.aprendeapp.utils.PreferencesHelper

class RecursoFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecursoFormBinding
    private val repository = RecursoRepository()
    private var recursoId: String? = null
    private var esEdicion: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecursoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarToolbar()
        configurarSpinner()
        cargarDatos()
        configurarBotones()
    }

    private fun configurarToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun configurarSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.tipos_recursos, // CORREGIDO: tipos_recursos con "s"
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipo.adapter = adapter
    }

    private fun cargarDatos() {
        @Suppress("DEPRECATION")
        val recurso = intent.getParcelableExtra<Recurso>("RECURSO")

        if (recurso != null) {
            esEdicion = true
            recursoId = recurso.id
            binding.tvTitle.text = getString(R.string.edit_resource) // CORREGIDO: edit_resource

            binding.etTitulo.setText(recurso.titulo)
            binding.etDescripcion.setText(recurso.descripcion)

            val tipos = resources.getStringArray(R.array.tipos_recursos) // CORREGIDO: tipos_recursos con "s"
            val posicion = tipos.indexOfFirst { it.equals(recurso.tipo, ignoreCase = true) }
            binding.spinnerTipo.setSelection(posicion.takeIf { it >= 0 } ?: 0)

            binding.etEnlace.setText(recurso.enlace)
            binding.etImagen.setText(recurso.imagen)
        } else {
            binding.tvTitle.text = getString(R.string.new_resource) // CORREGIDO: new_resource
        }
        binding.etTitulo.error = null
        binding.etDescripcion.error = null
        binding.etEnlace.error = null
        binding.etImagen.error = null
    }

    private fun configurarBotones() {
        binding.btnGuardar.setOnClickListener {
            if (validar()) {
                guardar()
            }
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun validar(): Boolean {
        // ... (el resto del método de validación parece estar bien)
        val titulo = binding.etTitulo.text.toString().trim()
        val descripcion = binding.etDescripcion.text.toString().trim()
        val enlace = binding.etEnlace.text.toString().trim()
        val imagen = binding.etImagen.text.toString().trim()

        if (titulo.isEmpty()) {
            binding.etTitulo.error = getString(R.string.error_titulo)
            return false
        }

        if (descripcion.isEmpty()) {
            binding.etDescripcion.error = getString(R.string.error_descripcion)
            return false
        }

        if (enlace.isEmpty()) {
            binding.etEnlace.error = getString(R.string.error_enlace)
            return false
        }

        if (!android.util.Patterns.WEB_URL.matcher(enlace).matches()) {
            binding.etEnlace.error = getString(R.string.error_enlace_invalido)
            return false
        }

        if (imagen.isEmpty()) {
            binding.etImagen.error = getString(R.string.error_imagen)
            return false
        }

        return true
    }

    private fun guardar() {
        val titulo = binding.etTitulo.text.toString().trim()
        val descripcion = binding.etDescripcion.text.toString().trim()
        val enlace = binding.etEnlace.text.toString().trim()
        val imagen = binding.etImagen.text.toString().trim()

        val tipoPos = binding.spinnerTipo.selectedItemPosition
        val tipos = resources.getStringArray(R.array.tipos_recursos) // CORREGIDO: tipos_recursos con "s"
        val tipo = if (tipoPos != AdapterView.INVALID_POSITION && tipoPos < tipos.size) tipos[tipoPos] else tipos[0]

        binding.progressBar.visibility = View.VISIBLE
        binding.btnGuardar.isEnabled = false

        lifecycleScope.launch {
            val usuarioId = PreferencesHelper.getUserId(this@RecursoFormActivity)

            val recurso = Recurso(
                id = recursoId,
                titulo = titulo,
                descripcion = descripcion,
                tipo = tipo,
                enlace = enlace,
                imagen = imagen,
                usuarioId = usuarioId
            )

            val resultado = if (esEdicion && recursoId != null) {
                repository.actualizarRecurso(recursoId!!, recurso)
            } else {
                repository.crearRecurso(recurso)
            }

            resultado.onSuccess {
                Toast.makeText(
                    this@RecursoFormActivity,
                    if (esEdicion) getString(R.string.recurso_actualizado) else getString(R.string.recurso_creado),
                    Toast.LENGTH_SHORT
                ).show()
                setResult(RESULT_OK)
                finish()
            }.onFailure { error ->
                Toast.makeText(
                    this@RecursoFormActivity,
                    "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            runOnUiThread {
                binding.progressBar.visibility = View.GONE
                binding.btnGuardar.isEnabled = true
            }
        }
    }
}
