package com.example.testcompose1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 数据库类
@Database(entities = [TodoEntity::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() { // 必须是抽象类
    abstract fun todoDao(): TodoDao // DAO方法必须是抽象的

    companion object {
        @Volatile
        private var INSTANCE: TodoDatabase? = null

        fun getInstance(context: Context): TodoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "todo_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}