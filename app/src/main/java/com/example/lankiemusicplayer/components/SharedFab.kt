package com.example.lankiemusicplayer.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.lankiemusicplayer.R
import com.example.lankiemusicplayer.ui.theme.ThemeAccent


enum class FabMode {
    HOME,
    SHUFFLE
}

private val MiniPlayerHeight = 90.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedFab(
    mode: FabMode,
    accent: ThemeAccent,
    isDarkMode: Boolean,

    listState: LazyListState? = null,
    onShuffle: (() -> Unit)? = null,
    onSearch: (() -> Unit)? = null,
    onRefresh: (() -> Unit)? = null,
) {

    var expanded by rememberSaveable { mutableStateOf(false) }

    BackHandler(expanded) { expanded = false }

    // Scroll-aware visibility
    val fabVisible by remember(listState) {
        derivedStateOf {
            listState?.let {
                it.firstVisibleItemIndex == 0 || !it.canScrollForward
            } ?: true
        }
    }

    val alpha = if (fabVisible || expanded) 1f else 0.3f
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val logoRes = accentLogo(
        accent = accent,
        isDarkMode = isDarkMode
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                end = 16.dp,
                bottom = MiniPlayerHeight + 16.dp
            )
    ) {

        when (mode) {

            // =========================
            // HOME MODE (MENU FAB)
            // =========================
            FabMode.HOME -> {

                FloatingActionButtonMenu(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    expanded = expanded,
                    button = {

                        ToggleFloatingActionButton(
                            checked = expanded,
                            onCheckedChange = { expanded = !expanded },
                            modifier = Modifier
                                .graphicsLayer { this.alpha = alpha }
                                .animateFloatingActionButton(
                                    visible = fabVisible || expanded,
                                    alignment = Alignment.BottomEnd
                                ),

                            containerColor = { primaryColor }

                        ) {

                            if (expanded) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = onPrimaryColor
                                )
                            } else {
                                Icon(
                                    painter = painterResource(logoRes),
                                    contentDescription = "Menu",
                                    tint = Color.Unspecified
                                )
                            }
                        }
                    }
                ) {

                    // 🔥 SEARCH ITEM (WHITE BACKGROUND)
                    FloatingActionButtonMenuItem(
                        onClick = {
                            expanded = false
                            onSearch?.invoke()
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        icon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        },
                        text = { Text("Search") }
                    )

                    // 🔥 REFRESH ITEM (WHITE BACKGROUND)
                    FloatingActionButtonMenuItem(
                        onClick = {
                            expanded = false
                            onRefresh?.invoke()
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        icon = {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                        },
                        text = { Text("Refresh") }
                    )
                }
            }

            // =========================
            // SHUFFLE MODE (SINGLE FAB)
            // =========================
            FabMode.SHUFFLE -> {

                FloatingActionButton(
                    onClick = { onShuffle?.invoke() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .graphicsLayer { this.alpha = alpha },

                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle"
                    )
                }
            }
        }
    }
}
private fun accentLogo(
    accent: ThemeAccent,
    isDarkMode: Boolean
): Int {

    return when (accent) {

        ThemeAccent.Default -> {
            if (isDarkMode)
                R.drawable.lankie_l_dark
            else
                R.drawable.lankie_l
        }

        ThemeAccent.Pink -> {
            if (isDarkMode)
                R.drawable.lankie_pink_dark
            else
                R.drawable.lankie_pink
        }

        ThemeAccent.Orange -> {
            if (isDarkMode)
                R.drawable.lankie_orange_dark
            else
                R.drawable.lankie_orange
        }

        ThemeAccent.Blue -> {
            if (isDarkMode)
                R.drawable.lankie_blue_dark
            else
                R.drawable.lankie_blue
        }

        ThemeAccent.Green -> {
            if (isDarkMode)
                R.drawable.lankie_green_dark
            else
                R.drawable.lankie_green
        }

        ThemeAccent.Purple -> {
            if (isDarkMode)
                R.drawable.lankie_purple_dark
            else
                R.drawable.lankie_purple
        }
    }
}