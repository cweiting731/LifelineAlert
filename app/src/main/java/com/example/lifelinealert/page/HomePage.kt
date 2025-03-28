package com.example.lifelinealert.page

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.lifelinealert.R
import com.example.lifelinealert.data.UserProfile

@Preview (showBackground = true)
@Composable
fun HomePage() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp, 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopBar(context)
        Banner()
    }
}

@Composable
fun TopBar(context: Context) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val userProfile = remember { UserProfile(context) }
    LaunchedEffect(key1 = Unit) {
        userProfile.imageUriFlow.collect() { savedUri ->
            savedUri?.let { imageUri = Uri.parse(it) }
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = imageUri?.let { rememberAsyncImagePainter(it) }
                ?: painterResource(id = R.drawable.profile_user_image_default_picture),
            contentDescription = "user image",
            modifier = Modifier
                .size(60.dp)
                .padding(10.dp, 10.dp)
                .clip(CircleShape),
        )
        Text(
            text = "Name",
            textAlign = TextAlign.Left,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f)
        )
        Box(modifier = Modifier.clip(CircleShape)) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "notification",
                modifier = Modifier
                    .background(Color.LightGray)
                    .size(40.dp)
                    .padding(5.dp)
            )
        }
    }
}

@Composable
fun Banner() {
    val times = 20 // 時間or次數
    val backgroundColor = Color(0xFF214489)
    Box(
        modifier = Modifier
            .fillMaxWidth(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
    ) {
        Column {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 15.dp),
                text = "恭喜你\n你這個月拯救了救護車",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                color = Color.White
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp),
                text = "${times}次",
                textAlign = TextAlign.Center,
                fontSize = 50.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}