package com.example.testcompose1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// 待办事项数据实体类
@Entity(tableName = "todo_items")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val isCompleted: Boolean = false, // 是否已完成
    val createdAt: Long = System.currentTimeMillis()
)