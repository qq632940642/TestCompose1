package com.example.testcompose1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.example.testcompose1.ui.theme.MyAppTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme(
                darkTheme = ThemeManager.isDarkTheme // 覆盖系统设置
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



