package com.example.lifelinealert.page

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.lifelinealert.R
import com.example.lifelinealert.data.UserProfile
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun SettingPage() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp, 20.dp), 
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PersonalProfile(context = context)
        SettingSection()
    }   
}

@Composable
fun PersonalProfile(context: Context) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val userProfile = remember { UserProfile(context) }
    val scope = rememberCoroutineScope()
    val backgroundColor = Color(0xFFA5C3FF)

    LaunchedEffect(key1 = Unit) {
        userProfile.imageUriFlow.collect() { savedUri ->
            savedUri?.let { imageUri = Uri.parse(it) }
        }
    }

    lateinit var cropImageLauncher: ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult>

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // uri not null and go to crop image
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
            scope.launch {
                userProfile.saveImageUri(result.uriContent.toString())
            }
            imageUri = result.uriContent
            Log.v("profile_image", "crop image successfully")
        } else
            Log.v("profile_image", "crop image fail")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = imageUri?.let { rememberAsyncImagePainter(it) }
                ?: painterResource(id = R.drawable.profile_user_image_default_picture),
            contentDescription = "user image",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape),
        )

        Spacer(modifier = Modifier.width(20.dp))

        Column {
            Text(text = "Name", fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "edit",
                fontSize = 20.sp,
                color = Color(0xFF214489),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { /* TODO: */ })
        }
    }
}

@Composable
fun SettingSection() {
    val settingTitleList = listOf(
        "音量",
        "通知",
        "Setting 3",
        "Setting 4",
        "Setting 5",
        "Setting 6",
        "Setting 7",
    )
    val detailPage = listOf(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
    )

//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(20.dp))
//            .background(Color.LightGray)
//            .padding(15.dp)
//    ) {
//
//    }
    LazyColumn {
        itemsIndexed(settingTitleList) {index: Int, item: String ->
            SettingItem(title = item, detailPage = detailPage[index])
        }
    }

}

@Composable
fun SettingItem(title: String, detailPage: Any?) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(15.dp))
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(Color.LightGray))
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(15.dp))
        Row(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 20.sp, modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "detail")
        }
    }
}