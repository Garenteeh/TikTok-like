package com.example.tiktokapp.models

data class Country(
    val name: String,
    val code: String,
    val phoneCode: String

)

/**
 * Retourne le drapeau emoji d'un pays à partir de son code ISO alpha-2
 * Exemple : "FR" -> 🇫🇷
 */
fun Country.flagEmoji(): String {
    return code
        .uppercase()
        .map { char ->
            Character.codePointAt(charArrayOf(char), 0) - 0x41 + 0x1F1E6
        }.joinToString("") { codePoint ->
            String(Character.toChars(codePoint))
        }
}

