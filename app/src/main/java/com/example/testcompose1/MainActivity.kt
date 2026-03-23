package com.example.testcompose1

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testcompose1.ui.theme.MyAppTheme
import com.example.testcompose1.ui.theme.TestCompose1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            MyAppTheme(
                darkTheme = ThemeManager.isDarkTheme // 覆盖系统设置
                ,dynamicColor = false // 暂时禁用动态颜色。
            ) {
                todoJobList()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun todoJobList(
    viewModel: TodoViewModel = viewModel()  // 获取ViewModel实例。 在同一个activity作用域中是单例。
) {
    var showInfiniteList by remember { mutableStateOf(true) }
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
                items(items = todoItems) { item ->
                    TodoItem(item = item
                        , onDelete = { viewModel.removeItem(item) })
                }
            }
        }
    }
}

@Composable
fun TodoItem(item: String, onDelete: () -> Unit  // 添加删除回调，删除逻辑放在上层。即把回调传给里面的按钮。
 ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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

