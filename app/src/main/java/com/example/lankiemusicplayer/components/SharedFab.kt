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

enum class FabMode {
    HOME,
    SHUFFLE
}

private val MiniPlayerHeight = 90.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedFab(
    mode: FabMode,
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

                            containerColor = { Color.White }

                        ) {

                            if (expanded) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.Black
                                )
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.lankie_l),
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
                        containerColor = Color.White,
                        contentColor = Color.Black,
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
                        containerColor = Color.White,
                        contentColor = Color.Black,
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