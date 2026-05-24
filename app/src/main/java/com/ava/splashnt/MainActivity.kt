package com.ava.splashnt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.ava.splashnt.ui.DetailsScreen
import com.ava.splashnt.ui.HomeScreen
import com.ava.splashnt.ui.splash.SplashContent
import com.ava.splashnt.ui.theme.SplashntTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SplashntTheme {
                var showSplash by rememberSaveable { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    delay(500)
                    showSplash = false
                }
                Crossfade(
                    targetState = showSplash,
                    label = "splash"
                ) { shouldShowSplash ->
                    if(shouldShowSplash) {
                        SplashContent()
                    } else {
                        val backstack = rememberNavBackStack(HomeScreen)
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
    }
}