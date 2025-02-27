package com.example.lifelinealert

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lifelinealert.page.MapPage
import com.example.lifelinealert.page.PointPage
import com.example.lifelinealert.page.ProfilePage
import com.example.lifelinealert.utils.manager.IMPORTANCE_HIGH
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

        NotificationManager.createNotificationChannel(
            context = this,
            name = "foreground",
            channelId = "foreground",
            importance = IMPORTANCE_HIGH,
            descriptionText = "foreground",
            soundUri = null,
            audioAttributes = null
        )

//        notificationManager.sendNotification(this, "System", "OnCreate")

//        val gpsForegroundService = Intent(this, GpsForegroundService::class.java)
//        startForegroundService(gpsForegroundService)

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

    Scaffold(
//        bottomBar = { BottomNavigationBar(navController) },
        snackbarHost = { SnackbarHost(hostState = SnackbarManager.snackbarHostState) },
    ) { innerPadding ->
        NavHostContainer(navController,
            Modifier
                .padding(innerPadding)
                .fillMaxSize())

        Column {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .weight(1f, true))
            BottomNavigationBar(navController = navController)
        }
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
    Row (
        modifier = Modifier
            .padding(10.dp, 10.dp, 10.dp, 30.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(color = Color(0xCCF2EDF7)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val currentRoute = getCurrentRoute(navController = navController)
        val selectedColor = Color(0xFF6650a4)
        val unselectedColor = Color(0xFF000000)

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
                        tint = if (currentRoute == page.route) selectedColor else unselectedColor
                    )
                }
            )
        }
    }
}

//@Composable
//fun BottomNavigationBar(navController: NavHostController) {
//    val pages = listOf(
//        Page.Map,
//        Page.Profile,
//        Page.Point
//    )
//    NavigationBar(
//        modifier = Modifier
//            .height(100.dp)
//            .padding(10.dp)
//            .clip(RoundedCornerShape(20.dp)),
//
//        containerColor = Color(0xCCF2EDF7),
//
//    ) {
//        val currentRoute = getCurrentRoute(navController = navController)
//        val selectedColor = Color(0xFF6650a4)
//        val unselectedColor = Color(0xFF000000)
//
//        Row(
//            modifier = Modifier.fillMaxSize(),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            pages.forEach { page ->
//                NavigationBarItem(
//                    modifier = Modifier.padding(10.dp),
//                    onClick = {
//                        if (page.route != currentRoute) {
//                            navController.navigate(page.route)
//                        }
//                    },
//                    selected = (currentRoute == page.route),
//                    label = { Text(text = stringResource(id = page.title), fontSize = 12.sp) },
//                    icon = {
//                        Icon(
//                            painter = painterResource(id = page.icon),
//                            contentDescription = stringResource(id = page.title),
//                            tint = if (currentRoute == page.route)  selectedColor else unselectedColor
//                        )
//                    },
//
//                )
//            }
//        }
//    }
//}

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
