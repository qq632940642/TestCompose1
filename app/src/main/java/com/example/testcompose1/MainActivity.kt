package com.example.testcompose1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import com.example.testcompose1.ui.theme.MyAppTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.testcompose1.data.PreferencesManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 手动创建 PreferencesManager 和 SettingsViewModel
        val preferencesManager = PreferencesManager(applicationContext)
        val settingsViewModel = SettingsViewModel(preferencesManager)
        setContent {
            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()
            MyAppTheme(
                darkTheme = isDarkTheme // 覆盖系统设置
                ,dynamicColor = false // 暂时禁用动态颜色。
            ) {
                // 导航控制器
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "list"
                ) {
                    composable("list") {
                        TodoListScreen(
                            settingsViewModel = settingsViewModel,
                            onNavigateToDetail = { itemId ->
                                navController.navigate("detail/$itemId")
                            }
                        )
                    }

                    // detail/{itemId} 是带参数的页面路径
                    composable("detail/{itemId}") { backStackEntry ->
                        val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                        TodoDetailScreen(
                            itemId = itemId,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}



