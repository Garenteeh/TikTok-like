package com.example.tiktokapp.data.utils

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object UserUtils {

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    private val phoneRegex = "^\\+?[0-9]{8,15}$".toRegex()

    const val MIN_PASSWORD_LENGTH = 12


    fun validateRequiredField(value: String, fieldName: String, minLength: Int = 3): String? {
        return when {
            value.isBlank() -> "Le $fieldName est requis"
            value.length < minLength -> "Le $fieldName doit contenir au moins $minLength caractères"
            else -> null
        }
    }

    fun validateRegexField(value: String, regex: Regex, fieldName: String): String? {
        if(value.isBlank()) {
            if(fieldName == "email") return "L'email est requis"
            return "Le $fieldName est requis"
        }
        if(!regex.matches(value)) return ("Le $fieldName est invalide")
        return null
    }

    fun validatePassword(password: String): String? {
        if (password.isBlank()) return "Mot de passe requis"
        if (password.length < MIN_PASSWORD_LENGTH) return "Au moins $MIN_PASSWORD_LENGTH caractères requis"

        val hasUpper = password.any { it.isUpperCase() }
        val hasLower = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }

        return when {
            !hasUpper -> "Doit contenir au moins une majuscule"
            !hasLower -> "Doit contenir au moins une minuscule"
            !hasDigit -> "Doit contenir au moins un chiffre"
            !hasSpecial -> "Doit contenir au moins un caractère spécial"
            else -> null
        }
    }

    fun validateBirthDate(date: String): String? {
        if(date.isBlank()) return "Date de naissance requise"
        if (!Regex("""\d{2}/\d{2}/\d{4}""").matches(date)) {
            return "Format attendu : JJ/MM/AAAA"
        }

        val (d, m, y) = date.split("/").map { it.toInt() }

        if (m !in 1..12) return "Date invalide"
        if (y !in 1900..2025) return "Date invalide"

        val maxDay = when (m) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> {
                if (isLeapYear(y)) 29 else 28
            }
            else -> return "Date invalide"
        }

        if (d !in 1..maxDay) {
            return "Date invalide"
        }

        return null
    }

    fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }



    /**
     * Valide tous les champs et retourne une map des erreurs
     * La clé est le nom du champ, la valeur est le message d'erreur
     * @param firstName Prénom de l'utilisateur
     * @param lastName Nom de l'utilisateur
     * @param username Pseudo de l'utilisateur
     * @param email Email de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     * @param phone Numéro de téléphone de l'utilisateur
     * @param country Pays de l'utilisateur
     * @param birthDate Date de naissance de l'utilisateur
     * @return Map des erreurs de validation
     */
    fun validateAll(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        phone: String,
        country: String,
        birthDate: String
    ): MutableMap<String, String> {
        val errors = mutableMapOf<String, String>()

        validateRequiredField(firstName, "prénom", minLength = 2)?.let { errors["firstName"] = it }
        validateRequiredField(lastName, "nom", minLength = 2)?.let { errors["lastName"] = it }
        validateRequiredField(username, "pseudo", minLength = 2)?.let { errors["username"] = it }
        validateRequiredField(country, "pays")?.let { errors["country"] = it }
        validateRegexField(email, emailRegex, "email")?.let { errors["email"] = it }
        validateRegexField(phone, phoneRegex, "numéro de téléphone")?.let { errors["phone"] = it }
        validatePassword(password)?.let { errors["password"] = it }
        validateBirthDate(birthDate)?.let { errors["birthDate"] = it }
        if(confirmPassword.isEmpty()) errors["confirmPassword"] = "Confirmation du mot de passe requise"
        if(password != confirmPassword)
            errors["confirmPassword"] = "Les mots de passe ne correspondent pas"
        return errors
    }

    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
    fun generateSalt(length: Int = 16): String {
        val random = SecureRandom()
        val salt = ByteArray(length)
        random.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    fun hashPasswordWithSalt(password: String, saltBase64: String, iterations: Int = 65536, keyLength: Int = 256): String {
        val salt = Base64.getDecoder().decode(saltBase64)
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLength)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val key = skf.generateSecret(spec).encoded
        return key.fold("") { str, it -> str + "%02x".format(it) }
    }

}