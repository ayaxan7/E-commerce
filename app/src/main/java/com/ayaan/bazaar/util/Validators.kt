package com.ayaan.bazaar.util

object Validators {

    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }

    fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Name is required"
            name.length < 2 -> "Name must be at least 2 characters"
            else -> null
        }
    }

    fun validateTitle(title: String): String? {
        return when {
            title.isBlank() -> "Title is required"
            title.length < 3 -> "Title must be at least 3 characters"
            title.length > 100 -> "Title must be less than 100 characters"
            else -> null
        }
    }

    fun validatePrice(price: String): String? {
        return when {
            price.isBlank() -> "Price is required"
            price.toDoubleOrNull() == null -> "Invalid price format"
            price.toDouble() <= 0 -> "Price must be greater than 0"
            else -> null
        }
    }

    fun validateDescription(description: String): String? {
        return when {
            description.isBlank() -> "Description is required"
            description.length < 10 -> "Description must be at least 10 characters"
            description.length > 1000 -> "Description must be less than 1000 characters"
            else -> null
        }
    }

    fun validateCity(city: String): String? {
        return when {
            city.isBlank() -> "City is required"
            city.length < 2 -> "City name must be at least 2 characters"
            else -> null
        }
    }

    fun validateYear(year: String): String? {
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        return when {
            year.isBlank() -> "Year is required"
            year.toIntOrNull() == null -> "Invalid year format"
            year.toInt() < 1900 -> "Year cannot be before 1900"
            year.toInt() > currentYear -> "Year cannot be in the future"
            else -> null
        }
    }
}
