package sv.edu.udb.aprendeapp.utils

object PasswordValidator {
    fun validate(password: String): ValidationResult {
        val errors = mutableListOf<String>()

        if (password.length < 8) {
            errors.add("Mínimo 8 caracteres")
        }
        if (!password.any { it.isUpperCase() }) {
            errors.add("Al menos una mayúscula")
        }
        if (!password.any { it.isLowerCase() }) {
            errors.add("Al menos una minúscula")
        }
        if (!password.any { it.isDigit() }) {
            errors.add("Al menos un número")
        }
        if (!password.any { it in "!@#\$%^&*" }) {
            errors.add("Al menos un carácter especial (!@#\$%^&*)")
        }

        return if (errors.isEmpty()) {
            ValidationResult(true)
        } else {
            ValidationResult(false, errors)
        }
    }

    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String> = emptyList()
    )
}