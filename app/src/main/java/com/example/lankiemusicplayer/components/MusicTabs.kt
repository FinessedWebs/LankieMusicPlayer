package com.example.lankiemusicplayer.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*

@Composable
fun MusicTabs(
    onTabSelected: (Int) -> Unit
) {

    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        "Recently Added",
        "Recently Played",
        "Most Played"
    )

    Column {

        PrimaryTabRow(selectedTabIndex = selectedTab) {

            tabs.forEachIndexed { index, title ->

                Tab(
                    selected = selectedTab == index,
                    onClick = {
                        selectedTab = index
                        onTabSelected(index)
                    },
                    text = { Text(title) }
                )
            }
        }
    }
}