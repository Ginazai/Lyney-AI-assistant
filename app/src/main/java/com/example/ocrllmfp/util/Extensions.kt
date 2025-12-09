package com.example.ocrllmfp.util

fun String.truncate(limit: Int): String =
    if (this.length <= limit) this else this.substring(0, limit) + "…"
