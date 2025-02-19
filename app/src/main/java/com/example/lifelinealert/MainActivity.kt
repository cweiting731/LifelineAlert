package com.example.lifelinealert

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lifelinealert.foreground.AlertDialog
import com.example.lifelinealert.page.MapPage
import com.example.lifelinealert.page.PointPage
import com.example.lifelinealert.page.ProfilePage


class MainActivity : ComponentActivity() {
    private var showDialog = mutableStateOf(false)
    private var alertDialogTitle = mutableStateOf("警告")
    private var alertDialogMessage = mutableStateOf("OnPause")
    private val notificationManager = com.example.lifelinealert.foreground.NotificationManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        notificationManager.requestNotificationPermission(this) // 請求發送訊息權限
        notificationManager.createNotificationChannel(this)

//        notificationManager.sendNotification(this, "System", "OnCreate")

        setContent {
            MainPage()
            AlertDialog(showDialog = showDialog, title = alertDialogTitle, message = alertDialogMessage)
        }

    }

    override fun onPause() {
        super.onPause()
        Log.v("lowerSystem", "pause")

//        sendNotification(this)
//        showDialog.value = true

    }

    override fun onStop() {
        super.onStop()
        Log.v("lowerSystem", "stop")
//        val serviceIntent = Intent(this, ForeGroundService::class.java)
//        ContextCompat.startForegroundService(this, serviceIntent)
    }
}

@Preview(showBackground = true)
@Composable
fun MainPage() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHostContainer(navController, Modifier.padding(innerPadding))
    }

}

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

//@Preview(showBackground = true)
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val pages = listOf(
        Page.Map,
        Page.Profile,
        Page.Point
    )
    NavigationBar {
        val currentRoute = getCurrentRoute(navController = navController)
        pages.forEach { page ->
            NavigationBarItem(
                onClick = { navController.navigate(page.route) },
                selected = (currentRoute == page.route),
                label = { Text(text = stringResource(id = page.title)) },
                icon = {
                    Icon(
                        painter = painterResource(id = page.icon),
                        contentDescription = stringResource(id = page.title),

                    )
                })
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

@Composable
fun getCurrentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
