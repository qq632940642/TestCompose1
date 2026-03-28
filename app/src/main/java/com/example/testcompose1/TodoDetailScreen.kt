package com.example.testcompose1

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// 代办事项详情页面
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    itemId: String, // 接收参数
    onBack: () -> Unit // 返回回调
) {
    var detailText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // 模拟加载详情（副作用）
    LaunchedEffect(itemId) { //  LaunchedEffect是Compose专用的协程启动器,跟随组件生命周期。而viewModelScope.launch只用在viewModel里
        delay(1000) // 模拟网络请求
        detailText = "详情内容：$itemId 是一个待办事项哦"
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("待办详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column {
                    Text(
                        text = "待办ID: $itemId",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = detailText)
                }
            }
        }
    }
}