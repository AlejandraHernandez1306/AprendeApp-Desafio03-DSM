package sv.edu.udb.aprendeapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recurso(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("titulo")
    val titulo: String = "",

    @SerializedName("descripcion")
    val descripcion: String = "",

    @SerializedName("tipo")
    val tipo: String = "",

    @SerializedName("enlace")
    val enlace: String = "",

    @SerializedName("imagen")
    val imagen: String = "",

    @SerializedName("usuarioId")
    val usuarioId: String? = null
) : Parcelable