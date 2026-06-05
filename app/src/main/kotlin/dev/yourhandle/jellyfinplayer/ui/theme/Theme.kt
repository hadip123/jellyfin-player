package dev.yourhandle.jellyfinplayer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PureWhite,
    secondary = IceBlue,
    background = NearBlack,
    surface = SurfaceDark,
    onPrimary = NearBlack,
    onSecondary = NearBlack,
    onBackground = PureWhite,
    onSurface = PureWhite,
    surfaceVariant = SurfaceCard
)

@Composable
fun JellyfinPlayerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
