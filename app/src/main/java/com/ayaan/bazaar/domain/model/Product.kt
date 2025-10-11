package com.ayaan.bazaar.domain.model

import java.util.Calendar
import java.util.Date

data class Product(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val mrp: Double = 0.0,
    val askingPrice: Double = 0.0,
    val description: String = "",
    val city: String = "",
    val year: Int = 0,
    val condition: String = "",
    val imageUrls: List<String> = emptyList(),
    val coverImageUrl: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val ownerId: String = "",
    val ownerEmail: String = "",
    val createdAt: Long= System.currentTimeMillis()
)

enum class ProductCategory(val displayName: String) {
    ELECTRONICS("Electronics"),
    CLOTHING("Clothing"),
    FURNITURE("Furniture"),
    VEHICLES("Vehicles"),
    BOOKS("Books"),
    SPORTS("Sports"),
    HOME("Home & Garden"),
    OTHER("Other")
}

enum class ProductCondition(val displayName: String) {
    NEW("New"),
    LIKE_NEW("Like New"),
    GOOD("Good"),
    FAIR("Fair"),
    POOR("Poor")
}
