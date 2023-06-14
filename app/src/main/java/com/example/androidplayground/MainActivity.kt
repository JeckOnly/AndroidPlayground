package com.example.androidplayground

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.example.androidplayground.ui.theme.AndroidPlaygroundTheme
import com.example.androidplayground.util.noIndicationClickable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidPlaygroundTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SpaceScrollView()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidPlaygroundTheme {
        Greeting("Android")
    }
}

@Deprecated("用fake数据填充")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpaceScrollView() {

    val state = rememberLazyListState()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val scope = rememberCoroutineScope()
    val itemWidth = screenWidth / 5
    val itemWidthPx = with(density) {
        itemWidth.toPx()
    }
    val firstItemIndex by remember { derivedStateOf { state.firstVisibleItemIndex } }
    val midIndex by remember(firstItemIndex) {
        derivedStateOf {
            state.layoutInfo.visibleItemsInfo.run {
                val firstVisibleIndex = firstItemIndex
                if (isEmpty()) -1f else firstVisibleIndex + ((last().index - firstVisibleIndex) / 2).toFloat()
            }
        }
    }


// If you'd like to customize either the snap behavior or the layout provider
    val snappingLayout = remember(state) { SnapLayoutInfoProvider(state) }
    val flingBehavior = rememberSnapFlingBehavior(snappingLayout)

    LazyRow(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        state = state,
        flingBehavior = flingBehavior
    ) {
        items(20) { index ->
            if (index == 0) {
                Box(modifier = Modifier
                    .width(3 * itemWidth)
                    .noIndicationClickable {
                        scope.launch {
                            state.animateScrollToItem(index)
                        }
                    }, contentAlignment = Alignment.CenterEnd) {
                    Box(
                        modifier = Modifier
                            .width(itemWidth),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(index.toString(), fontSize = if (firstItemIndex == 0) 32.sp else 20.sp)
                    }
                }
            } else if (index == 19) {
                Box(modifier = Modifier
                    .width(3 * itemWidth)
                    .noIndicationClickable {
                        scope.launch {
                            state.animateScrollToItem(index)
                        }
                    }, contentAlignment = Alignment.CenterStart) {
                    Box(
                        modifier = Modifier
                            .width(itemWidth),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(index.toString(), fontSize = if (firstItemIndex == 19) 32.sp else 20.sp)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .width(itemWidth)
                        .noIndicationClickable {
                            scope.launch {
                                state.animateScrollToItem(index, -(2 * itemWidthPx).toInt())

                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(index.toString(), fontSize = if (midIndex.toInt() == index) 32.sp else 20.sp)
                }
            }
        }
    }
}

@Composable
fun TextItem(text: String) {
    Surface(color = Color.Cyan, border = BorderStroke(1.dp, Color.Blue)) {
        Text(text = text, modifier = Modifier.padding(30.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
fun PreviewSpaceScrollView() {
    SpaceScrollView()
}