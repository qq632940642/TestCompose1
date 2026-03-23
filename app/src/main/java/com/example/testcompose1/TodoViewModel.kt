package com.example.testcompose1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {
    // MutableStateFlow 为可修改的状态流
    // 使用 StateFlow 持有待办列表，这样Compose可以观察
    private val _todoItems = MutableStateFlow<List<String>>(emptyList())
    // StateFlow 是MutableStateFlow的父接口, 只读。 即对外暴露这个只读的
    val todoItems: StateFlow<List<String>> = _todoItems.asStateFlow()

    init {
        viewModelScope.launch {
            // 模拟加载数据
            delay(2000)
            _todoItems.value = listOf("人中吕布，马中赤兔", "花无百日红", "人无再少年")
        }
    }

    fun addItem(item: String) {
        if (item.isNotBlank()) {
            _todoItems.value += item
        }
    }

    fun removeItem(item: String) {
        _todoItems.value -= item
    }
}