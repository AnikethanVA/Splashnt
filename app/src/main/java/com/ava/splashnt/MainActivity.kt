package com.ava.splashnt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.ava.splashnt.ui.DetailsScreen
import com.ava.splashnt.ui.HomeScreen
import com.ava.splashnt.ui.theme.SplashntTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplashntTheme {
                val backstack = remember { mutableStateListOf<NavKey>(HomeScreen) }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavDisplay(
                        backStack = backstack,
                        onBack = { backstack.removeLastOrNull() },
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator()
                        ),
                        entryProvider = entryProvider {
                            entry<HomeScreen> {
                                com.ava.splashnt.ui.home.HomeScreen(modifier = Modifier.padding(innerPadding)) { image ->
                                    backstack.add(DetailsScreen(image))
                                }
                            }

                            entry<DetailsScreen> {
                                com.ava.splashnt.ui.detail.DetailsScreen(image = it.image)
                            }
                        }
                    )
                }
            }
        }
    }
}