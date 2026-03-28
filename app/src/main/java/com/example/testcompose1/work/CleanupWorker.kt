package com.example.testcompose1.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class CleanupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) { // CoroutineWorker 是一个抽象类，它继承自 Worker，并使用协程来执行工作

    override suspend fun doWork(): Result {
//        val cleanDays = params.inputData.getInt("cleanDays", 3) // 获取参数
        // 模拟清理工作
        println("CleanupWorker: 正在清理过期待办...")
        // 模拟耗时操作
        delay(2000)
        println("CleanupWorker: 清理完成")
        return Result.success()
    }
}