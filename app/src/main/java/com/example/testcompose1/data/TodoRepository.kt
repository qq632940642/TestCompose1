package com.example.testcompose1.data

import kotlinx.coroutines.flow.Flow

// 数据仓库
class TodoRepository(private val dao: TodoDao) {
    fun getAllTodos(): Flow<List<TodoEntity>> = dao.getAllTodos()

    suspend fun addTodo(title: String) {
        dao.insert(TodoEntity(title = title))
    }

    suspend fun deleteTodo(todo: TodoEntity) {
        dao.delete(todo)
    }

    suspend fun updateTodo(todo: TodoEntity) {
        dao.update(todo)
    }
}