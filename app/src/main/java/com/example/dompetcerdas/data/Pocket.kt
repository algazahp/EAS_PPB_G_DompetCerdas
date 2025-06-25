package com.example.dompetcerdas.data

import java.util.UUID

data class Pocket(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    var balance: Double
)
