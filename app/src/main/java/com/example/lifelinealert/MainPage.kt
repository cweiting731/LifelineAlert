package com.example.lifelinealert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lifelinealert.page.MapPage
import com.example.lifelinealert.page.PointPage
import com.example.lifelinealert.page.ProfilePage
import com.example.lifelinealert.utils.manager.SnackbarManager

sealed class Page(
    val route: String,
    val title: Int,
    val icon: Int
) {
    object Map : Page("map", R.string.page_map_title, R.drawable.ic_navigation_bar_map)
    object Profile :
        Page("profile", R.string.page_profile_title, R.drawable.ic_navigation_bar_profile)

    object Point : Page("point", R.string.page_point_title, R.drawable.ic_navigation_bar_point)
}

@Composable
fun MainPage(viewModel: MainActivityViewModel) {
    val navController = rememberNavController()
    val showController by viewModel.showController

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = SnackbarManager.snackbarHostState) },
    ) { innerPadding ->
        NavHostContainer(navController,
            Modifier
                .padding(innerPadding)
                .fillMaxSize())

        if (showController) {
            Column {
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, true))
                BottomNavigationBar(navController = navController, viewModel = viewModel)
            }
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun BottomNavigationBar(navController: NavHostController, viewModel: MainActivityViewModel) {
    val pages = listOf(
        Page.Map,
        Page.Profile,
        Page.Point
    )
    Row (
        modifier = Modifier
            .padding(10.dp, 10.dp, 10.dp, 30.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(color = Color(0xCCF2EDF7)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val currentRoute by viewModel.pageRoute
        val selectedColor = Color(0xFF6650a4)
        val unselectedColor = Color(0xFF000000)

        pages.forEach { page ->
            NavigationBarItem(
                onClick = {
                    if (page.route != currentRoute) {
                        navController.navigate(page.route)
                        viewModel.setPageRoute(page.route)
                    }
                },
                selected = (currentRoute == page.route),
                label = { Text(text = stringResource(id = page.title)) },
                icon = {
                    Icon(
                        painter = painterResource(id = page.icon),
                        contentDescription = stringResource(id = page.title),
                        tint = if (currentRoute == page.route) selectedColor else unselectedColor
                    )
                }
            )
        }
    }
}

@Composable
fun NavHostContainer(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = Page.Map.route) {
        composable(Page.Map.route) { MapPage() }
        composable(Page.Profile.route) { ProfilePage() }
        composable(Page.Point.route) { PointPage() }
    }
}