package sv.edu.udb.aprendeapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import sv.edu.udb.aprendeapp.databinding.ItemRecursoBinding
import sv.edu.udb.aprendeapp.model.Recurso

class RecursoAdapter(
    private val onEditClick: (Recurso) -> Unit,
    private val onDeleteClick: (Recurso) -> Unit
) : ListAdapter<Recurso, RecursoAdapter.RecursoViewHolder>(RecursoDiffCallback()) {
    inner class RecursoViewHolder(private val binding: ItemRecursoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recurso: Recurso) {

            binding.tvTitulo.text = recurso.titulo
            binding.tvDescripcion.text = recurso.descripcion

            Glide.with(binding.root.context)
                .load(recurso.imagen)
                .into(binding.ivRecurso)

            binding.ivEdit.setOnClickListener { onEditClick(recurso) }
            binding.ivDelete.setOnClickListener { onDeleteClick(recurso) }

            itemView.setOnClickListener {
                val enlace = recurso.enlace
                if (enlace.isNotEmpty()) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(enlace))
                        itemView.context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(itemView.context, "No se puede abrir el enlace", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecursoViewHolder {
        val binding = ItemRecursoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecursoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecursoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}
class RecursoDiffCallback : DiffUtil.ItemCallback<Recurso>() {
    override fun areItemsTheSame(oldItem: Recurso, newItem: Recurso): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Recurso, newItem: Recurso): Boolean {
        return oldItem == newItem
    }
}