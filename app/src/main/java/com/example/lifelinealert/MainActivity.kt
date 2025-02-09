package com.example.lifelinealert

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
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
                        contentDescription = stringResource(id = page.title)
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
@Composable
fun MapPage() {
    val taiwan = LatLng(22.999973101427155, 120.21985214463398)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(taiwan, 15f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = taiwan),
            title = "library",
            snippet = "國立成功大學圖書館"
        )
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
        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
    ) {
        SearchBar()
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier
            .padding(24.dp, 2.dp, 24.dp, 6.dp)
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
            modifier
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
            Modifier
                .padding(6.dp)
                .fillMaxHeight()
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(Color.LightGray)
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