package com.example.testcompose1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// 数据库类
@Database(entities = [TodoEntity::class], version = 2, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() { // 必须是抽象类
    abstract fun todoDao(): TodoDao // DAO方法必须是抽象的

    companion object {
        @Volatile
        private var INSTANCE: TodoDatabase? = null

        // 数据库升级用
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 执行SQL，为todo_items表添加名为description的TEXT类型列，并设置默认值为空字符串
                database.execSQL("ALTER TABLE todo_items ADD COLUMN description TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getInstance(context: Context): TodoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "todo_database"
                ).addMigrations(MIGRATION_1_2) // 应用迁移规则
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}