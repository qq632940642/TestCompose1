package com.example.testcompose1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.testcompose1.data.TodoEntity
import com.example.testcompose1.data.TodoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoViewModel(
    private val repository: TodoRepository
) : ViewModel() {

    /** 分页待办列表（懒加载，每页 [TodoRepository.PAGE_SIZE] 条） */
    val todoPagingFlow: Flow<PagingData<TodoEntity>> =
        repository.getTodosPaged().cachedIn(viewModelScope)

    // 统计信息
    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount.asStateFlow()

    private val _completedCount = MutableStateFlow(0)
    val completedCount: StateFlow<Int> = _completedCount.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getTotalCount().collect { count ->
                _totalCount.value = count
            }
        }

        viewModelScope.launch {
            repository.getCompletedCount().collect { count ->
                _completedCount.value = count
            }
        }
    }

    fun addTodo(title: String) {
        if (title.isNotBlank()) {
            viewModelScope.launch {
                repository.addTodo(title)
            }
        }
    }

    fun deleteTodo(todo: TodoEntity) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
        }
    }

    fun toggleComplete(todo: TodoEntity) {
        viewModelScope.launch {
            repository.updateTodo(todo.copy(isCompleted = !todo.isCompleted))
        }
    }

    // 单个待办
    private val _currentTodo = MutableStateFlow<TodoEntity?>(null)
    val currentTodo: StateFlow<TodoEntity?> = _currentTodo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadTodoById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val todo = repository.getTodoById(id)
            _currentTodo.value = todo
            _isLoading.value = false
        }
    }

    fun updateTodo(todo: TodoEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateTodo(todo)
            _isLoading.value = false
        }
    }
}
