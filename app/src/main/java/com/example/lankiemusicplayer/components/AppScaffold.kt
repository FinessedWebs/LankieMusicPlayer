package com.example.lankiemusicplayer.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.layout.padding
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String,
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    floatingActionButton: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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