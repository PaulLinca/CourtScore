package com.linca.courtscore.domain.model

enum class Point(private val pointValue: Int?) {
    LOVE(0),
    FIFTEEN(15),
    THIRTY(30),
    FORTY(40),
    ADVANTAGE(null);

    fun asDisplayString(): String = pointValue?.toString() ?: "AD"
}