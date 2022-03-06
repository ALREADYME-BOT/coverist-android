package dev.yjyoon.coverist.data.remote.model

data class Book(
    val title: String,
    val author: String,
    val genre: String,
    val subGenre: String,
    val tags: List<String>,
    val publisher: String?
)