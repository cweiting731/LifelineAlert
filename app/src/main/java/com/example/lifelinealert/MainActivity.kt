package com.example.lifelinealert

import android.content.pm.PackageManager
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainPage()
        }
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

//@Preview(showBackground = true)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapPage() {
    val cameraPermissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(cameraPermissionState.status) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    val taiwan = LatLng(22.999973101427155, 120.21985214463398)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(taiwan, 15f)
    }

    val bottomBarHeight = 80.dp // default NavigationBarHeight is 80.dp
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomBarHeight)
    ) {
        if (cameraPermissionState.status.isGranted) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true), // 啟用當前位置
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = true // 啟用定位按鈕
                )
            ) {
                Marker(
                    state = MarkerState(position = taiwan),
                    title = "library",
                    snippet = "國立成功大學圖書館"
                )
            }
        } else {
//            Text(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .wrapContentSize(Alignment.Center),
//                text = "No permission"
//            )
            Button(
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", "com.example.lifelinealert", null)
                    })
                },
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("需要精準位置權限授權，點擊跳轉設定")
            }
        }
    }
}


// Profile Page use ProfileImage and ProfileText
@Preview(showBackground = true)
@Composable
fun ProfilePage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
    ) {
        ProfileImage()
        ProfileText()
    }
}

@Composable
fun ProfileImage() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    lateinit var cropImageLauncher: ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult>

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            cropImageLauncher.launch(
                CropImageContractOptions(uri, CropImageOptions().apply {
                    cropShape = CropImageView.CropShape.OVAL
                    aspectRatioX = 1
                    aspectRatioY = 1
                    fixAspectRatio = true
                })
            )
            Log.v("profile_image", "launch image successfully")
        }
    }

    // crop image after lanch
    cropImageLauncher = rememberLauncherForActivityResult(
        contract = CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
            Log.v("profile_image", "crop image successfully")
        } else
            Log.v("profile_image", "crop image fail")
    }
    Row(
        modifier = Modifier.padding(50.dp)
    ) {
        Button(
            onClick = {
                launcher.launch("image/*")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
            modifier = Modifier.clip(RoundedCornerShape(10.dp))

        ) {
            Image(
                painter = imageUri?.let { rememberAsyncImagePainter(it) }
                    ?: painterResource(id = R.drawable.profile_user_image_default_picture),
                contentDescription = "user image",
                modifier = Modifier
                    .size(150.dp)
                    .padding(10.dp)
                    .clip(CircleShape),
            )
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun ProfileText() {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.profile_name_title),
                fontSize = 24.sp,
                modifier = Modifier.padding(70.dp, 0.dp, 20.dp, 0.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.profile_name_default),
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.profile_phone_number_title),
                fontSize = 24.sp,
                modifier = Modifier.padding(70.dp, 0.dp, 20.dp, 0.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.profile_phone_number_default),
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.profile_email_title),
                fontSize = 24.sp,
                modifier = Modifier.padding(70.dp, 0.dp, 20.dp, 0.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.profile_email_default),
                fontSize = 24.sp,
                textAlign = TextAlign.Left
            )
        }
    }
}


// PointPage
@Preview(showBackground = true)
@Composable
fun PointPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp, 20.dp, 12.dp, 0.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
    ) {
        SearchBar()
        IndexBar()
        CommodityBar()
    }
}

@Composable
fun SearchBar() {
    Row(
        modifier = Modifier
            .padding(12.dp, 2.dp, 12.dp, 6.dp)
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var searchText by remember {
            mutableStateOf("")
        }
        BasicTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .padding(start = 24.dp)
                .weight(1f),
            textStyle = TextStyle(fontSize = 15.sp)
        ) {
            if (searchText.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.place_holder_search),
                    color = Color.Gray,
                    fontSize = 15.sp
                )
            }
            it()
        }
        Box(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxHeight()
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(Color.LightGray)
                .clickable {
                    /* TODO */
                }
        ) {
            Icon(
                imageVector = Icons.Default.Search, contentDescription = "search",
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
            )
        }

    }
}

@Composable
fun IndexBar() {
    //
    val indexTitle = stringArrayResource(id = R.array.index_bar_title)
    var selected by remember {
        mutableStateOf(0)
    }

    LazyRow(
        modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 8.dp),
        contentPadding = PaddingValues(8.dp, 0.dp)
    ) {
        itemsIndexed(indexTitle) { index, title ->
            Column(
                modifier = Modifier
                    .padding(12.dp, 4.dp)
                    .width(IntrinsicSize.Max)
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    color = if (index == selected) {
                        Color.Black
                    } else {
                        Color.LightGray
                    },
                    modifier = Modifier.clickable {
                        selected = index
                        /* TODO */
                    }
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(
                            if (index == selected) {
                                Color.Black
                            } else {
                                Color.Transparent
                            }
                        )
                )
            }
        }
    }
}

@Composable
fun CommodityBar() {
    /*
        TODO: need to load commodity from server
    * */
    // test
    val commodityNameList = listOf(
        "商品1",
        "商品2",
        "商品3",
        "商品4",
        "商品5",
        "商品6",
        "商品7",
        "商品8",
        "商品9",
    )
    val commodityImageList = listOf(
        R.drawable.commodity_test_image,
        R.drawable.commodity_test_image,
        R.drawable.commodity_test_image,
        R.drawable.commodity_test_image,
        R.drawable.commodity_test_image,
        R.drawable.commodity_test_image,
        R.drawable.commodity_test_image,
        R.drawable.commodity_test_image,
        R.drawable.commodity_test_image,
    )
    val commodityDescriptionList = listOf(
        "this is description ha ha ha ~~~",
        "this is description ha ha ha ~~~",
        "this is description ha ha ha ~~~",
        "this is description ha ha ha ~~~",
        "this is description ha ha ha ~~~",
        "this is description ha ha ha ~~~",
        "this is description ha ha ha ~~~",
        "this is description ha ha ha ~~~",
        "this is description ha ha ha ~~~",
    )

    LazyColumn(
        modifier = Modifier.padding(top = 10.dp)
    ) {
        itemsIndexed(commodityNameList) { index, name ->
//            CommodityItem(
//                imageID = commodityImageList[index],
//                name = name,
//                description = commodityDescriptionList[index]
//            )
            CommodityItemTwo(
                imageID = commodityImageList[index],
                name = name,
                description = commodityDescriptionList[index]
            )
        }
    }
}

@Composable
fun CommodityItem(imageID: Int, name: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 5.dp)
    ) {
        Image(
            painter = painterResource(id = imageID),
            contentDescription = name,
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(2.dp, Color.Black, RectangleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = name, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = description,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxSize()
            )
        }

    }
}


@Composable
fun CommodityItemTwo(imageID: Int, name: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = imageID),
            contentDescription = name,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(2.dp, Color.Black, RectangleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = name, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = description,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxSize()
            )
        }

    }
}