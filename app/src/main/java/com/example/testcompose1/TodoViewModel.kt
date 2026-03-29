package com.example.testcompose1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testcompose1.data.TodoEntity
import com.example.testcompose1.data.TodoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoViewModel(
    private val repository: TodoRepository // 数据仓库
) : ViewModel() {
//    // MutableStateFlow 为可修改的状态流
//    // 使用 StateFlow 持有待办列表，这样Compose可以观察
//    private val _todoItems = MutableStateFlow<List<String>>(emptyList())
//    // StateFlow 是MutableStateFlow的父接口, 只读。 即对外暴露这个只读的
//    val todoItems: StateFlow<List<String>> = _todoItems.asStateFlow()

    private val _todos = MutableStateFlow<List<TodoEntity>>(emptyList())
    val todos: StateFlow<List<TodoEntity>> = _todos.asStateFlow()

    init {
//        viewModelScope.launch {
//            // 模拟加载数据
//            delay(2000)
//            _todoItems.value = listOf("人中吕布，马中赤兔", "花无百日红", "人无再少年", "人生像客旅", "日光之下无新鲜事")
//        }
        loadTodos()
    }

    private fun loadTodos() {
        viewModelScope.launch {
            repository.getAllTodos().collect { list ->
                _todos.value = list
            }
        }
    }

//    fun addItem(item: String) {
//        if (item.isNotBlank()) {
//            _todoItems.value += item
//        }
//    }

//    fun removeItem(item: String) {
//        _todoItems.value -= item
//    }

    fun addTodo(title: String) {
        if (title.isNotBlank())
            viewModelScope.launch {
                repository.addTodo(title)
            }
    }

    fun deleteTodo(todo: TodoEntity) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
        }
    }

    fun toggleComplete(todo: TodoEntity) {
        viewModelScope.launch {
            // TodoEntity是数据类，自带copy方法，拷贝一个对象，切换完成状态
            repository.updateTodo(todo.copy(isCompleted = !todo.isCompleted))
        }
    }
}