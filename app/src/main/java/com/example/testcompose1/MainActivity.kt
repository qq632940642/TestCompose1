package com.example.testcompose1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType

import com.example.testcompose1.ui.theme.MyAppTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.testcompose1.data.PreferencesManager
import com.example.testcompose1.data.TodoDatabase
import com.example.testcompose1.data.TodoRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 手动创建 PreferencesManager 和 SettingsViewModel
        val preferencesManager = PreferencesManager(applicationContext)
        val settingsViewModel = SettingsViewModel(preferencesManager)

        val db = TodoDatabase.getInstance(this)
        val dao = db.todoDao()
        val repository = TodoRepository(dao)
        val viewModel = TodoViewModel(repository)

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
                            viewModel = viewModel,
                            settingsViewModel = settingsViewModel,
                            onNavigateToDetail = { id ->
                                navController.navigate("detail/$id")
                            }
                        )
                    }

                    // detail/{itemId} 是带参数的页面路径
                    composable("detail/{todoId}",
                            arguments = listOf(navArgument("todoId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val todoId = backStackEntry.arguments?.getInt("todoId") ?: 0
                        TodoDetailScreen(
                            todoId  = todoId,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}



