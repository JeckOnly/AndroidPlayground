package com.example.androidplayground

import android.media.Image
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import arrow.core.Either
import arrow.core.left
import arrow.core.raise.Raise
import arrow.core.right
import com.example.androidplayground.ui.theme.AndroidPlaygroundTheme
import dagger.hilt.android.AndroidEntryPoint

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
                    Greeting("Android")
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

object UserNotFound
data class User(val id: Long)

val user: Either<UserNotFound, User> = User(1).right()


fun main() {
    user.fold(
        { println("User not found") },
        { println("User found: $it") }
    )

    val a: A<Image, ImageView> = B<Image>()
}

open class A <out L, out R> {


}

class B <out T>: A<T, Nothing>() {


}