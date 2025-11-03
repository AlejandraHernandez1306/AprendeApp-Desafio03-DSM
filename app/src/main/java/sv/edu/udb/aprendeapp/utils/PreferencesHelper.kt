package sv.edu.udb.aprendeapp.utils

import android.content.Context

object PreferencesHelper {
    private const val PREF_NAME = "AprendeAppPrefs"
    private const val KEY_USER_ID = "userId"

    fun saveUserId(context: Context, userId: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getUserId(context: Context): String? = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_USER_ID, null)

    fun clearUserId(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().remove(KEY_USER_ID).apply()
    }
}