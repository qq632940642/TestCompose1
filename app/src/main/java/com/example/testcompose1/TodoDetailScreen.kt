package com.example.testcompose1

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

// 代办事项详情页面
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    todoId: Int,
    viewModel: TodoViewModel = viewModel(),
    onBack: () -> Unit
) {
    val currentTodo by viewModel.currentTodo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var titleInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }
    var isModified by remember { mutableStateOf(false) }

    // 加载数据
    LaunchedEffect(todoId) {
        viewModel.loadTodoById(todoId)
    }

    // 当数据加载完成后，初始化输入框
    LaunchedEffect(currentTodo) {
        currentTodo?.let {
            titleInput = it.title
            descriptionInput = it.description ?: ""
            isModified = false
        }
    }

    // 监听输入变化，判断是否有修改
    LaunchedEffect(titleInput, descriptionInput) {
        // 关键：将委托属性赋值给局部变量，以便智能转换
        val todo = currentTodo
        if (todo != null) {
            isModified = titleInput != todo.title ||
                    descriptionInput != (todo.description ?: "")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("编辑待办") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    // 同样在这里捕获局部变量
                    val todo = currentTodo
                    Button(
                        onClick = {
                            if (isModified && todo != null) {
                                val updatedTodo = todo.copy(
                                    title = titleInput,
                                    description = descriptionInput
                                )
                                viewModel.updateTodo(updatedTodo)
                                onBack()
                            }
                        },
                        enabled = isModified && !isLoading
                    ) {
                        Text("保存")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // 再次捕获局部变量用于 UI 显示
            val todo = currentTodo
            if (todo == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("待办项不存在")
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("标题") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = descriptionInput,
                        onValueChange = { descriptionInput = it },
                        label = { Text("详情") },
                        placeholder = { Text("请输入详情") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            }
        }
    }
}