package com.example.lankiemusicplayer.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.lankiemusicplayer.components.MiniPlayer
import com.example.lankiemusicplayer.screens.AllSongsScreen
import com.example.lankiemusicplayer.screens.ArtistDetailScreen
import com.example.lankiemusicplayer.screens.ArtistsScreen
import com.example.lankiemusicplayer.screens.FavoritesScreen
import com.example.lankiemusicplayer.screens.HomeScreen
import com.example.lankiemusicplayer.screens.PlayerScreen
import com.example.lankiemusicplayer.screens.PlaylistDetailScreen
import com.example.lankiemusicplayer.screens.PlaylistsScreen
import com.example.lankiemusicplayer.screens.QueueScreen
import com.example.lankiemusicplayer.viewmodel.PlayerViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val context = LocalContext.current
    val application = context.applicationContext as android.app.Application

    val hideMiniPlayerRoutes = listOf(
        "player",
        "PlayerScreen"
    )

    // Shared ViewModel for the whole app
    val playerViewModel: PlayerViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory(application)
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        NavHost(
            navController = navController,
            startDestination = "home"
        ) {

            composable("home") {
                HomeScreen(
                    navController = navController,
                    playerViewModel = playerViewModel,
                    onOpenPlayer = {
                        navController.navigate("player")
                    }
                )
            }

            composable("player") {
                PlayerScreen(
                    viewModel = playerViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onShowQueue = {
                        navController.navigate("queue")
                    },
                    onNavigateToArtist = { artist ->
                        navController.navigate("artist_detail/$artist")
                    },
                    onNavigateToAllSongs = { uri ->
                        val encoded = URLEncoder.encode(uri, StandardCharsets.UTF_8.toString())
                        navController.navigate("allsongs?scrollTo=$encoded")
                    }
                )
            }

            composable(
                route = "allsongs?scrollTo={scrollTo}",
                arguments = listOf(
                    navArgument("scrollTo") {
                        type = NavType.StringType
                        nullable = true
                    }
                )
            ) { backStackEntry ->

                val scrollToUri = backStackEntry.arguments?.getString("scrollTo")

                AllSongsScreen(
                    playerViewModel = playerViewModel,
                    scrollToUri = scrollToUri,
                    onOpenPlayer = {
                        navController.navigate("player")
                    }
                )
            }

            composable("favorites") {
                FavoritesScreen(playerViewModel = playerViewModel)
            }

            composable("queue") {
                QueueScreen(
                    viewModel = playerViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("playlists") {
                PlaylistsScreen(
                    navController = navController,
                    viewModel = playerViewModel
                )
            }

            composable("playlist_detail/{id}") {
                val id = it.arguments?.getString("id")?.toLongOrNull() ?: return@composable

                PlaylistDetailScreen(
                    playlistId = id,
                    navController = navController,
                    viewModel = playerViewModel
                )
            }

            composable("artists") {
                ArtistsScreen(
                    navController = navController,
                    viewModel = playerViewModel
                )
            }

            composable("artist_detail/{artistName}") {

                val artistName = it.arguments?.getString("artistName") ?: return@composable

                ArtistDetailScreen(
                    artistName = artistName,
                    viewModel = playerViewModel,
                    navController = navController
                )
            }


        }

        // 🔥 GLOBAL MINIPLAYER (ONLY ONE IN APP)
        if (currentRoute !in hideMiniPlayerRoutes) {
            MiniPlayer(
                onOpenPlayer = { navController.navigate("player") },
                viewModel = playerViewModel,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
            )
        }
    }
}