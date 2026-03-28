package com.example.testcompose1

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections.rotate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Preview
@Composable
fun TodoListScreen(
    viewModel: TodoViewModel = viewModel(),  // 获取ViewModel实例。 在同一个activity作用域中是单例。
    onNavigateToDetail: (String) -> Unit = {}
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp.value
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val offsetX = -(screenWidthPx * 3).toInt()  // 从屏幕左侧3倍宽度外滑入

    var showInfiniteList by remember { mutableStateOf(false) }
    if (showInfiniteList) {
        // 显示无限滚动列表，并提供一个返回按钮
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("无限滚动列表") },
                    navigationIcon = {
                        IconButton(onClick = { showInfiniteList = false }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                        }
                    }
                )
            }
        ) { innerPadding ->
            // 给 InfiniteListPage 添加内边距
            Box(modifier = Modifier.padding(innerPadding)) {
                InfiniteListPage()
            }
        }
    } else { // 显示原待办事项列表
        val context = LocalContext.current

        // 使用 remember 和 mutableStateOf 保存输入框的文本
        var text by remember { mutableStateOf("") }
        // 使用 mutableStateListOf 保存待办项列表
//    val todoItems = remember { mutableStateListOf<String>() }
        // 将 StateFlow 转换为 Compose 可观察的 State
        val todoItems by viewModel.todoItems.collectAsState()

//        // 使用 map 存储每个项的"正在删除"状态
//        var deletingItems by remember { mutableStateOf(setOf<String>()) }
        // 获取协程作用域，用于延迟删除
        val scope = rememberCoroutineScope()
        // 管理每个项的可见性，初始为 true，新添加的项先设为 false，然后立即设为 true
        val itemVisibility = remember { mutableStateMapOf<String, Boolean>() }

        // 同步 itemVisibility 与 todoItems：为新增项添加初始 false，并在下一帧设为 true
        LaunchedEffect(todoItems) {
            todoItems.forEach { item ->
                if (!itemVisibility.containsKey(item)) {
                    // 新项：初始不可见
                    itemVisibility[item] = false
                    // 等待一帧，然后设为可见，触发进入动画
                    launch {
                        delay(50) // 短暂延迟，确保重组
                        itemVisibility[item] = true
                    }
                }
            }
            // 清理已删除的项
            itemVisibility.keys.retainAll(todoItems.toSet())
        }

        Column(modifier = Modifier.padding(16.dp)) {
            ThemeSwitch()  // 添加开关
            Spacer(modifier = Modifier.height(8.dp))
            // 文本输入框
            TextField(
                value = text,
                onValueChange = { text = it }, // 反向绑定，视图变化--> 数据变化
                label = { Text("输入待办事项") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface, // 获得焦点时的背景色
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant, // 失去焦点时，输入框背景色
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary, // 输入框底部下划线的颜色。
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            // 添加按钮
            Button(
                onClick = {
                    viewModel.addItem(text)
                    text = ""
                },
                shape = MaterialTheme.shapes.small,  // 使用主题形状
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // 容器背景色，按钮底色
                    contentColor = MaterialTheme.colorScheme.onPrimary // 内容颜色，按钮上文字 / 图标的颜色
                ),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("添加")
            }

            // 显示待办列表
            Spacer(modifier = Modifier.height(16.dp))
            Text("待办列表", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(items = todoItems
                ,key = { it }) //  item 唯一身份标识，防止动画错乱
                { item ->
                    val visible = itemVisibility[item] ?: true
                    // 为每个项添加动画。 AnimatedVisibility没起作用
                    AnimatedVisibility(
//                        visible = item !in deletingItems,  // 不在删除集合中才显示
                        visible = visible,
//                        enter = fadeIn(animationSpec = tween(500)) +
//                                slideInHorizontally(initialOffsetX = { -600 }
//                                    , animationSpec = tween(500)),
                        enter = fadeIn(animationSpec = tween(1500, easing = FastOutSlowInEasing)) +
                                slideInHorizontally(
                                    initialOffsetX = { -3000 },  // 固定大偏移量，从左侧 3000 像素外滑入
                                    animationSpec = tween(1500, easing = FastOutSlowInEasing)
                                ) +
                                scaleIn(
                                    initialScale = 0.1f,
                                    animationSpec = tween(1500, easing = FastOutSlowInEasing)
                                ),
                        exit = fadeOut(animationSpec = tween(500)) +
                                slideOutHorizontally(targetOffsetX = { 200 }
                                    , animationSpec = tween(500))
                    ) {
                        // SideEffect 是一个专门用于执行副作用的可组合函数。它的主要作用是在每次 重组（recomposition） 时，安全地执行那些不直接影响 UI、但需要与外部系统交互的操作（例如日志记录、埋点、更新非 Compose 管理的状态等）。
                        SideEffect {
                            println("Item $item 显示动画执行")
                        }
                        TodoItemRow(item = item
                            , onDelete = {
                                // 触发删除动画
                                itemVisibility[item] = false
                                scope.launch {
                                    delay(500)
                                    viewModel.removeItem(item)
//                                    deletingItems = deletingItems - item
                                    // 清理状态由 LaunchedEffect 的 retainAll 负责
                                }
                             }
                            , onClick = { onNavigateToDetail(item) }// 点击跳转
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodoItemRow(item: String, onDelete: () -> Unit  // 添加删除回调，删除逻辑放在上层。即把回调传给里面的按钮。
                , onClick: () -> Unit
                , modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }, // 现在 modifier 应该会叠加动画修饰符
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp // 这里传你要的默认高度
        ),
        shape = MaterialTheme.shapes.medium,  // 使用主题形状
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween // 横向布局子元素两端对齐，剩余空白空间平均分配到子元素之间
        ) {
            Text(text = item
                ,style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface)
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "删除"
                    , tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// 主题切换开关
@Composable
fun ThemeSwitch() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "深色模式",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(
            checked = ThemeManager.isDarkTheme,
            onCheckedChange = { ThemeManager.toggleTheme() }
        )
    }
}

// 为了允许手动切换深色/浅色模式，在应用中保存用户的选择，并在主题中读取. 后面改用DataStore保存
object ThemeManager {
    var isDarkTheme by mutableStateOf(false)
        private set

    fun toggleTheme() { // 切换是否为深色主题
        isDarkTheme = !isDarkTheme
    }
}