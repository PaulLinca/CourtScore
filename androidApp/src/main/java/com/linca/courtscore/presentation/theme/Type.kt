package com.linca.courtscore.presentation.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.linca.courtscorewear.R

val collegeCleanFontFamily = FontFamily(
    Font(R.font.college_clean_italic, FontWeight.Normal)
)

val logoStyle = TextStyle(
    fontFamily = collegeCleanFontFamily,
    fontSize = 18.sp
)