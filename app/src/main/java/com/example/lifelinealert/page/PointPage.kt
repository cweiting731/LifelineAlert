package com.example.lifelinealert.page

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifelinealert.R

@Preview(showBackground = true)
@Composable
fun PointPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp, 30.dp, 12.dp, 90.dp)
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
            .padding(12.dp, 12.dp, 12.dp, 6.dp)
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
            CommodityItem(
                imageID = commodityImageList[index],
                name = name,
                description = commodityDescriptionList[index]
            )
//            CommodityItemTwo(
//                imageID = commodityImageList[index],
//                name = name,
//                description = commodityDescriptionList[index]
//            )
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