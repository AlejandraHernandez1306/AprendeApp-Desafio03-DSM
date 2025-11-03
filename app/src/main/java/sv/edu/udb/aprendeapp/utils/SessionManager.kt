package sv.edu.udb.aprendeapp.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "AprendeAppPrefs"
        private const val USER_TOKEN = "user_token"
        private const val USER_NAME = "user_name"
    }

    /**
     * Guarda el token (ID del usuario) en las SharedPreferences.
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    /**
     * Guarda el nombre del usuario en las SharedPreferences.
     */
    fun saveUserName(name: String) {
        val editor = prefs.edit()
        editor.putString(USER_NAME, name)
        editor.apply()
    }

    /**
     * Obtiene el token (ID del usuario) guardado.
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    /**
     * Obtiene el nombre del usuario guardado.
     */
    fun fetchUserName(): String? {
        return prefs.getString(USER_NAME, null)
    }

    /**
     * Verifica si el usuario está logueado (si existe un token).
     */
    fun isLoggedIn(): Boolean {
        return fetchAuthToken() != null
    }

    /**
     * Borra todos los datos de la sesión.
     */
    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}
