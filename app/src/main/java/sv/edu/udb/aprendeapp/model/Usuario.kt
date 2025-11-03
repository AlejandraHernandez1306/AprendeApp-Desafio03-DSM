package sv.edu.udb.aprendeapp.model

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)