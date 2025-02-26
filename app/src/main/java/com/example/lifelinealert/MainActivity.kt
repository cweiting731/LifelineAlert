package com.example.lifelinealert

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lifelinealert.page.MapPage
import com.example.lifelinealert.page.PointPage
import com.example.lifelinealert.page.ProfilePage
import com.example.lifelinealert.utils.manager.NotificationManager
import com.example.lifelinealert.utils.manager.PermissionManager
import com.example.lifelinealert.utils.manager.SnackbarManager


class MainActivity : ComponentActivity() {
    private var showDialog = mutableStateOf(false)
    private var alertDialogTitle = mutableStateOf("警告")
    private var alertDialogMessage = mutableStateOf("OnPause")
    private val permissionRequestCode = 10
    private val left_channelId = "left_emergency"
    private val right_channelId = "right_emergency"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        PermissionManager.requestPermissions(this)
//        fineLocationPermissionHandler.requestPermission(this) // 請求發送位置權限
//        notificationManager.requestNotificationPermission(this) // 請求發送訊息權限

//        notificationManager.createNotificationChannel(this)

        NotificationManager.createNotificationChannel(this, "foreground_channel")
        NotificationManager.createNotificationChannel(this, left_channelId)
        NotificationManager.createNotificationChannel(this, right_channelId)

//        notificationManager.sendNotification(this, "System", "OnCreate")

        setContent {
            MainPage()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            PermissionManager.handlePermissionResult(this, permissions, grantResults)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.v("lowerSystem", "pause")

//        sendNotification(this)
//        showDialog.value = true

    }

    override fun onResume() {
        super.onResume()
        Log.v("lowerSystem", "resume")
    }

    override fun onRestart() {
        super.onRestart()
        Log.v("lowerSystem", "restart")
        PermissionManager.requestPermissions(this)
    }

    override fun onStop() {
        super.onStop()
        Log.v("lowerSystem", "stop")
//        val serviceIntent = Intent(this, ForeGroundService::class.java)
//        ContextCompat.startForegroundService(this, serviceIntent)
        Handler(Looper.getMainLooper()).postDelayed({
//            notificationManager.sendNotification(
//                this,
//                "警告",
//                "前方十字路口有救護車從右側出沒！請減速！"
//            )
            Log.v("lowerSystem", "notification testing")
            NotificationManager.sendNotificationEmergency(
                this,
                "警告",
                "前方路口有救護車從右側出沒！請減速！",
//                MainActivity::class.java,
                right_channelId,
                "right"
            )
//            notificationManager.sendNotification(this, "test", "test")
        }, 5000)
    }
}

@Preview(showBackground = true)
@Composable
fun MainPage() {

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        snackbarHost = { SnackbarHost(hostState = SnackbarManager.snackbarHostState) }
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
                onClick = {
                    if (page.route != currentRoute) {
                        navController.navigate(page.route)
                    }
                },
                selected = (currentRoute == page.route),
                label = { Text(text = stringResource(id = page.title)) },
                icon = {
                    Icon(
                        painter = painterResource(id = page.icon),
                        contentDescription = stringResource(id = page.title),
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

@Composable
fun getCurrentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
