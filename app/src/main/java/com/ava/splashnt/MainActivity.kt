package com.ava.splashnt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.ava.splashnt.ui.Destinations
import com.ava.splashnt.ui.DetailsScreen
import com.ava.splashnt.ui.HomeScreen
import com.ava.splashnt.ui.theme.SplashntTheme
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val navKeySaver = listSaver(
            save = {navKeyList ->
                navKeyList.map {  navKey ->
                    Json.encodeToString(navKey)
                }
            },
            restore = { navKeyList ->
                navKeyList.map { navKeyString ->
                    Json.decodeFromString<Destinations>(navKeyString)
                }.toMutableStateList()
            }
        )
        setContent {
            SplashntTheme {
                val backstack = rememberSaveable(saver = navKeySaver) { mutableStateListOf(HomeScreen) }
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