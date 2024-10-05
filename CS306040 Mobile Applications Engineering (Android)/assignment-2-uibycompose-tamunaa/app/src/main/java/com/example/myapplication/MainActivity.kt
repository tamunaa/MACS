package com.example.myapplication

import AppDetailScreen
import AppInfoItem
import AppSupportSection
import BetaTesterInfoSection
import CategoriesChips
import DataSafetySection
import HeaderSection
import MoreAppsToTrySection
import RatingsAndReviewsSection
import ReviewsList
import ScreensCarousel
import android.health.connect.datatypes.AppInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.AboutAppSection
import com.example.myapplication.ui.components.AppRatingsSection
import android.provider.ContactsContract.CommonDataKinds.Im
import androidx.annotation.RequiresApi
import com.example.myapplication.ui.components.RefundPolicySection
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    MainScreen();
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun MainScreen(){
    LazyColumn(
        contentPadding = PaddingValues(all = 16.dp)
    ){
        item { HeaderSection() }
        item { AppDetailScreen() }
        item { AppSupportSection() }
        item { BetaTesterInfoSection() }
        item { AboutAppSection() }
        item { CategoriesChips() }
        item { AppRatingsSection() }
        item { ScreensCarousel() }
        item { DataSafetySection() }
        item { RatingsAndReviewsSection() }
        item { ReviewsList() }
        item { MoreAppsToTrySection() }
        item { RefundPolicySection() }
    }
}