package com.ayaan.bazaar.domain.model

import java.util.Calendar
import java.util.Date

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val createdAt: Long= System.currentTimeMillis()
)
