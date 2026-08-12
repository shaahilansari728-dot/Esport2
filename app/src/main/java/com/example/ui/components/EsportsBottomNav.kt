package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EsportsCyan
import com.example.ui.theme.EsportsSurface
import com.example.ui.theme.EsportsSurfaceVariant
import com.example.ui.theme.EsportsTextPrimary
import com.example.ui.theme.EsportsTextSecondary

@Composable
fun EsportsBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = EsportsSurface,
        tonalElevation = 8.dp,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("esports_bottom_nav")
    ) {
        // Tab 0: Leaderboard
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Leaderboard"
                )
            },
            label = {
                Text(
                    text = "STANDINGS",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF381E72),
                selectedTextColor = EsportsCyan,
                indicatorColor = EsportsCyan,
                unselectedIconColor = EsportsTextSecondary,
                unselectedTextColor = EsportsTextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_leaderboard")
        )

        // Tab 1: Matches
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = "Matches"
                )
            },
            label = {
                Text(
                    text = "MATCHES",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF381E72),
                selectedTextColor = EsportsCyan,
                indicatorColor = EsportsCyan,
                unselectedIconColor = EsportsTextSecondary,
                unselectedTextColor = EsportsTextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_matches")
        )

        // Tab 2: Teams
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = "Teams"
                )
            },
            label = {
                Text(
                    text = "ROSTERS",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF381E72),
                selectedTextColor = EsportsCyan,
                indicatorColor = EsportsCyan,
                unselectedIconColor = EsportsTextSecondary,
                unselectedTextColor = EsportsTextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_teams")
        )

        // Tab 3: Admin Center
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Admin"
                )
            },
            label = {
                Text(
                    text = "ADMIN",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF381E72),
                selectedTextColor = EsportsCyan,
                indicatorColor = EsportsCyan,
                unselectedIconColor = EsportsTextSecondary,
                unselectedTextColor = EsportsTextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_admin")
        )
    }
}
