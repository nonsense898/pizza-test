package com.cpunks.pizzacatalog.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.cpunks.pizzacatalog.core.ui.R

val Figtree = FontFamily(
    Font(R.font.figtree_regular,   FontWeight.Normal),
    Font(R.font.figtree_semibold,  FontWeight.SemiBold),
    Font(R.font.figtree_bold,      FontWeight.Bold),
    Font(R.font.figtree_extrabold, FontWeight.ExtraBold),
)

val PizzaTypography = Typography(
    headlineMedium = TextStyle(fontFamily = Figtree, fontWeight = FontWeight.Bold,     fontSize = 22.sp, color = TextDark),
    titleLarge     = TextStyle(fontFamily = Figtree, fontWeight = FontWeight.Bold,     fontSize = 20.sp, color = TextDark),
    titleMedium    = TextStyle(fontFamily = Figtree, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextDark),
    bodyMedium     = TextStyle(fontFamily = Figtree, fontWeight = FontWeight.Normal,   fontSize = 13.sp, color = TextMedium, lineHeight = 18.sp),
    labelLarge     = TextStyle(fontFamily = Figtree, fontWeight = FontWeight.Bold,     fontSize = 15.sp, color = SurfaceWhite),
)
