package com.linca.courtscore.domain.model

import com.linca.courtscore.domain.model.Point.ADVANTAGE
import com.linca.courtscore.domain.model.Point.FIFTEEN
import com.linca.courtscore.domain.model.Point.FORTY
import com.linca.courtscore.domain.model.Point.LOVE
import com.linca.courtscore.domain.model.Point.THIRTY

enum class Point {
    LOVE,
    FIFTEEN,
    THIRTY,
    FORTY,
    ADVANTAGE;
}

fun pointToDisplayString(point: Point) = when (point) {
    LOVE -> "0"
    FIFTEEN -> "15"
    THIRTY -> "30"
    FORTY -> "40"
    ADVANTAGE -> "AD"
}

fun pointToTieBreakCount(p: Point): Int = when (p) {
    LOVE -> 0
    FIFTEEN -> 1
    THIRTY -> 2
    FORTY -> 3
    ADVANTAGE -> 4
}

fun tieBreakCountToPoint(count: Int): Point = when (count) {
    0 -> LOVE
    1 -> FIFTEEN
    2 -> THIRTY
    3 -> FORTY
    else -> ADVANTAGE
}