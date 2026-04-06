package com.example.testcompose1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.testcompose1.data.TodoEntity
import com.example.testcompose1.data.TodoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TodoViewModel(
    private val repository: TodoRepository
) : ViewModel() {

    /** 分页待办列表（懒加载，每页 [TodoRepository.PAGE_SIZE] 条） */
    val todoPagingFlow: Flow<PagingData<TodoEntity>> =
        repository.getTodosPaged().cachedIn(viewModelScope)

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
}
