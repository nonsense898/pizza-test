package com.cpunks.pizzacatalog.core.ui.theme

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColors = lightColorScheme(
    primary        = PizzaOrange,
    background     = BackgroundBeige,
    surface        = SurfaceWhite,
    onPrimary      = SurfaceWhite,
    onBackground   = TextDark,
    onSurface      = TextDark,
    secondary      = PizzaBrown,
    onSecondary    = SurfaceWhite,
)

@Composable
fun PizzaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography  = PizzaTypography
    ) {

        CompositionLocalProvider(
            LocalTextStyle provides LocalTextStyle.current.copy(fontFamily = Figtree),
            content = content
        )
    }
}
