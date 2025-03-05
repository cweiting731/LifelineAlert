package com.example.lifelinealert

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Rational
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lifelinealert.page.MapPage
import com.example.lifelinealert.page.PointPage
import com.example.lifelinealert.page.ProfilePage
import com.example.lifelinealert.utils.manager.IMPORTANCE_HIGH
import com.example.lifelinealert.utils.manager.NotificationManager
import com.example.lifelinealert.utils.manager.PermissionManager
import com.example.lifelinealert.utils.manager.SnackbarManager


class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainActivityViewModel
    private var showDialog = mutableStateOf(false)
    private var alertDialogTitle = mutableStateOf("警告")
    private var alertDialogMessage = mutableStateOf("OnPause")
    private val permissionRequestCode = 10
    private val left_channelId = "left_emergency"
    private val right_channelId = "right_emergency"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        // request permission
        PermissionManager.requestPermissions(this)

        // create notification channel
        NotificationManager.createNotificationChannel(
            context = this,
            name = "foreground",
            channelId = "foreground",
            importance = IMPORTANCE_HIGH,
            descriptionText = "foreground",
            soundUri = null,
            audioAttributes = null
        )

        setContent {
            MainPage(viewModel)
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

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        if (viewModel.pageRoute.value == "map") {
            val aspectRatio = Rational(40, 40)
            val pipBuilder = PictureInPictureParams.Builder().setAspectRatio(aspectRatio)
            enterPictureInPictureMode(pipBuilder.build())
            Log.v("pip", "pip mode")
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        if (isInPictureInPictureMode) {
            viewModel.setShowController(false)
        }
        else {
            viewModel.setShowController(true)
        }
    }
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