package com.example.lankiemusicplayer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.lankiemusicplayer.components.MiniPlayer
import com.example.lankiemusicplayer.viewmodel.PlayerViewModel

@Composable
fun CookingTimeScreen(
    playerViewModel: PlayerViewModel,
    onOpenPlayer: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        // Top 70% = Stove layout
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StoveTop()
                StoveTop()
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StoveTop()
                StoveTop()
            }
        }

        Spacer(Modifier.height(12.dp))

        // Bottom = Mini Player
        MiniPlayer(
            onOpenPlayer = onOpenPlayer,
            viewModel = playerViewModel,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun StoveTop() {
    Box(
        modifier = Modifier
            .size(140.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        )
    }
}