package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val FinSmartLightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = EmeraldOnContainer,
    secondary = SkyAdvisor,
    onSecondary = Color.White,
    secondaryContainer = SkyContainer,
    onSecondaryContainer = SkyOnContainer,
    tertiary = AmberHighlight,
    onTertiary = Color.White,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = AmberOnContainer,
    error = CoralDeep,
    onError = Color.White,
    errorContainer = CoralContainer,
    onErrorContainer = CoralOnContainer,
    background = SlateBackground,
    onBackground = SlateInk,
    surface = PureWhiteSurface,
    onSurface = SlateInk,
    surfaceVariant = SoftSlateSurfaceVariant,
    onSurfaceVariant = SlateMutedInk,
    outline = SlateOutline,
    outlineVariant = SlateOutline
)

val FinSmartShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun FinSmartEduTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FinSmartLightColorScheme,
        typography = FinSmartTypography,
        shapes = FinSmartShapes,
        content = content
    )
}
