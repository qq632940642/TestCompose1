package com.example.testcompose1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InfiniteListPage(
    viewModel: InfiniteListViewModel = viewModel()
) {
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 1. 控制下拉刷新的状态
    var isRefreshing by remember { mutableStateOf(false) }
    val refreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            // 下拉刷新时，清空列表并重新加载第一页
            isRefreshing = true
            // 这里简单重置：清空 ViewModel 中的数据，重新加载
            viewModel.reset()
            isRefreshing = false
        }
    )

    // 2. 监听滚动到底部，自动加载更多。 创建并记忆LazyColumn的滚动状态
    val listState = rememberLazyListState()
    // LaunchedEffect监听listState的变化，创建协程作用域
    LaunchedEffect(listState) {
        // 将"最后一个可见item的索引"转换成可监听的数据流Flow.
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            // 收集数据流的变化（每次滚动，这个值都会变）。 订阅数据流
            .collect { lastVisibleIndex ->
                // 如果最后一个可见项索引接近列表末尾（比如倒数第 3 项），且没有正在加载，则加载更多
                if (lastVisibleIndex != null &&
                    lastVisibleIndex >= items.size - 3 &&
                    !isLoading &&
                    items.size < 100) {
                    viewModel.loadMore()
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(refreshState) // 下拉刷新修饰符
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(items) { item ->
                ListItem(item = item)
            }

            // 当正在加载时，在列表末尾显示加载指示器
            if (isLoading) {
                item { // 插入单个列表项
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        // 下拉刷新指示器
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun ListItem(item: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = 2.dp
    ) {
        Text(
            text = item,
            modifier = Modifier.padding(16.dp)
        )
    }
}