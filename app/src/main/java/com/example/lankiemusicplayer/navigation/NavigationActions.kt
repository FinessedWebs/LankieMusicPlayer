package com.example.lankiemusicplayer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

class NavigationActions(
    private val navController: NavController
) {

    fun goHome() {
        navController.popBackStack("home", false)
    }

    fun goSleep() {
        navController.navigate("sleep")
    }

    fun goSearch() {
        navController.navigate("search")
    }

    fun goPlayer() {
        navController.navigate("player")
    }

    fun goPlaylistDetail(id: Long) {
        navController.navigate("playlist_detail/$id")
    }

    fun goArtistDetail(name: String) {
        navController.navigate("artist_detail/$name")
    }

    fun goCookingTime() {
        navController.navigate("cooking_time")
    }
}

@Composable
fun rememberNavigationActions(
    navController: NavController
): NavigationActions {
    return NavigationActions(navController)
}