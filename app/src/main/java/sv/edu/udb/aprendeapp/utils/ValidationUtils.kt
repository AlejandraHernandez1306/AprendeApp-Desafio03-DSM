package sv.edu.udb.aprendeapp.utils

object ValidationUtils {

    private val capitalLetterRegex = Regex(".*[A-Z].*")
    private val lowerCaseLetterRegex = Regex(".*[a-z].*")
    private val numberRegex = Regex(".*\\d.*")
    private val specialCharRegex = Regex(".*[!@#\$%^&*].*")

    fun isPasswordValid(password: String): Map<String, Boolean> {
        val results = mutableMapOf<String, Boolean>()

        results["length"] = password.length >= 8
        results["uppercase"] = password.matches(capitalLetterRegex)
        results["lowercase"] = password.matches(lowerCaseLetterRegex)
        results["number"] = password.matches(numberRegex)
        results["special"] = password.matches(specialCharRegex)

        return results
    }

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
