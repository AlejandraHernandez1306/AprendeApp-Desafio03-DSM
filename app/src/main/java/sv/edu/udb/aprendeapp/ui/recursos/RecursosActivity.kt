package sv.edu.udb.aprendeapp.ui.recursos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import sv.edu.udb.aprendeapp.R
import sv.edu.udb.aprendeapp.adapter.RecursoAdapter
import sv.edu.udb.aprendeapp.databinding.ActivityRecursosBinding
import sv.edu.udb.aprendeapp.model.Recurso
import sv.edu.udb.aprendeapp.repository.RecursoRepository
import sv.edu.udb.aprendeapp.ui.auth.LoginActivity
import sv.edu.udb.aprendeapp.utils.SessionManager

class RecursosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecursosBinding
    private lateinit var recursoAdapter: RecursoAdapter
    private val recursoRepository = RecursoRepository()
    private lateinit var sessionManager: SessionManager

    private var listaCompletaDeRecursos: List<Recurso> = emptyList()
    private var filtroTipoActual: String = "Todos"
    private var ordenAscendente = true

    private val formResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            cargarRecursos()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecursosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setSupportActionBar(binding.toolbar)
        configurarRecyclerView()
        configurarListeners()
        cargarRecursos()
    }

    private fun configurarRecyclerView() {
        recursoAdapter = RecursoAdapter(
            onEditClick = { recurso ->
                val intent = Intent(this, RecursoFormActivity::class.java).apply {
                    putExtra("RECURSO", recurso)
                }
                formResultLauncher.launch(intent)
            },
            onDeleteClick = { recurso ->
                mostrarDialogoEliminar(recurso)
            }
        )
        binding.rvRecursos.apply {
            layoutManager = LinearLayoutManager(this@RecursosActivity)
            adapter = recursoAdapter
        }
    }

    private fun configurarListeners() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, RecursoFormActivity::class.java)
            formResultLauncher.launch(intent)
        }

        binding.swipeRefresh.setOnRefreshListener {
            cargarRecursos()
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                actualizarListaFiltrada()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) {
                binding.chipTodos.isChecked = true
                filtroTipoActual = "Todos"
            } else {
                val chipId = checkedIds.first()
                val chip = group.findViewById<Chip>(chipId)
                filtroTipoActual = chip?.text.toString()
            }
            actualizarListaFiltrada()
        }
    }

    private fun cargarRecursos() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val resultado = recursoRepository.obtenerRecursos()
            resultado.onSuccess { listaRecursos ->
                listaCompletaDeRecursos = listaRecursos
                actualizarListaFiltrada()
            }.onFailure { error ->
                Toast.makeText(this@RecursosActivity, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
            binding.progressBar.visibility = View.GONE
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun actualizarListaFiltrada() {
        val textoBusqueda = binding.etSearch.text.toString().trim()
        var listaProcesada = listaCompletaDeRecursos

        if (filtroTipoActual != "Todos") {
            listaProcesada = listaProcesada.filter {
                it.tipo.equals(filtroTipoActual, ignoreCase = true)
            }
        }

        if (textoBusqueda.isNotEmpty()) {
            listaProcesada = listaProcesada.filter { recurso ->
                val esBusquedaPorId = textoBusqueda.all { it.isDigit() }
                if (esBusquedaPorId) {
                    recurso.id?.equals(textoBusqueda) == true
                } else {
                    recurso.titulo.contains(textoBusqueda, ignoreCase = true)
                }
            }
        }

        listaProcesada = if (ordenAscendente) {
            listaProcesada.sortedBy { it.titulo }
        } else {
            listaProcesada.sortedByDescending { it.titulo }
        }

        recursoAdapter.submitList(listaProcesada)
    }

    private fun mostrarDialogoEliminar(recurso: Recurso) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_resource))
            .setMessage(getString(R.string.delete_confirm))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                lifecycleScope.launch {
                    recurso.id?.let { id ->
                        val resultado = recursoRepository.eliminarRecurso(id)
                        resultado.onSuccess {
                            Toast.makeText(this@RecursosActivity, getString(R.string.recurso_eliminado), Toast.LENGTH_SHORT).show()
                            cargarRecursos()
                        }.onFailure { error ->
                            Toast.makeText(this@RecursosActivity, "Error al eliminar: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                mostrarDialogoLogout()
                true
            }
            R.id.action_sort -> {
                ordenAscendente = !ordenAscendente
                actualizarListaFiltrada()
                val mensaje = if (ordenAscendente) "Ordenado A-Z" else "Ordenado Z-A"
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mostrarDialogoLogout() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.logout_confirm))
            .setPositiveButton(getString(R.string.yes)) { _, _ -> cerrarSesion() }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun cerrarSesion() {
        sessionManager.clearSession()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
