package com.example.lankiemusicplayer.components

import android.R.attr.onClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.lankiemusicplayer.player.SleepTimerManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String,
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCookingTimeClick: () -> Unit,
    onSleepClick: () -> Unit,
    sleepTimerText: String? = null,
    floatingActionButton: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val sleepTimerTextState by SleepTimerManager.sleepTimerText

    ModalNavigationDrawer(

        drawerState = drawerState,

        drawerContent = {
            ModalDrawerSheet {

                Text("Navigation", style = MaterialTheme.typography.titleLarge)

                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onHomeClick()
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Search") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onSearchClick()
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onSettingsClick() // ✅ new callback
                    }
                )

                NavigationDrawerItem(

                    label = {

                        Column {

                            Text("Sleep Timer")

                            SleepTimerManager.sleepTimerText.value?.let {

                                Spacer(Modifier.height(2.dp))

                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },

                    selected = false,

                    onClick = {
                        scope.launch { drawerState.close() }
                        onSleepClick()
                    }
                )
                    /*selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onSleepClick()
                    }*/



                NavigationDrawerItem(
                    label = { Text("Cooking Time") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onCookingTimeClick()
                    }
                )

            }
        }

    ) {

        Scaffold(

            topBar = {

                TopAppBar(

                    title = { Text(title) },

                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },

                    actions = {

                        IconButton(onClick = onHomeClick) {
                            Icon(Icons.Default.Home, contentDescription = "Home")
                        }

                        IconButton(onClick = onSearchClick) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                )

                /*if (sleepTimerTextState != null) {

                    Surface(
                        onClick = onSleepClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = sleepTimerTextState!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            TextButton(
                                onClick = {
                                    SleepTimerManager.cancelTimer()
                                }
                            ) {
                                Text("Cancel")
                            }
                        }
                    }
                }*/
            },

            floatingActionButton = {
                floatingActionButton?.invoke()
            }

        ) { padding ->

            contentWrapper(padding, content)
        }
    }
}

@Composable
private fun contentWrapper(
    padding: PaddingValues,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.padding(padding)
    ) {
        content()
    }
}