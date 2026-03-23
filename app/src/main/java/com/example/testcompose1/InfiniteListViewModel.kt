package com.example.testcompose1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 创建无限滚动列表的数据源和 ViewModel
class InfiniteListViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<String>>(emptyList())
    val items: StateFlow<List<String>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentPage = 0
    private val pageSize = 20
    private val totalItems = 100  // 总共 100 条数据

    init {
        loadMore()
    }

    fun loadMore() {
        // 如果正在加载，或者已经加载完所有数据，则不再加载
        if (_isLoading.value || _items.value.size >= totalItems) return

        viewModelScope.launch(Dispatchers.IO) { // 启动协程，在io线程中执行
            _isLoading.value = true
            // 模拟网络延迟
            delay(1000)

            // 生成新数据
            val start = _items.value.size
            val end = minOf(start + pageSize, totalItems)
            val newItems = (start until end).map { "项目 $it" }

            _items.value += newItems
            _isLoading.value = false
        }
    }

    fun reset() {
        _items.value = emptyList()
        currentPage = 0
        loadMore()
    }
}