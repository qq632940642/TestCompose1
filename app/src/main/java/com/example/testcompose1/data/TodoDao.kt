package com.example.testcompose1.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// 数据访问对象
@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_items ORDER BY createdAt DESC")
    fun getAllTodos(): Flow<List<TodoEntity>> // 返回数据流，需要持续监听数据，不加suspend关键字

    @Insert
    suspend fun insert(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)

    @Update
    suspend fun update(todo: TodoEntity)
}