package com.linca.courtscore

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform