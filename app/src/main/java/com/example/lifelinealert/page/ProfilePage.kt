package com.example.lifelinealert.page

import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.lifelinealert.R

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
